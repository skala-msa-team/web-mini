package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialCreationAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TrialRepository trialRepository;
    @Autowired
    private TrialPartyRepository trialPartyRepository;

    @Test
    void createsPreparingPublicTrialWithTwoUnreadyParties() throws Exception {
        UserEntity author = userRepository.save(
                new UserEntity("author-" + UUID.randomUUID(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                author, "게시글", "내용", RelationshipType.COUPLE, true));
        String creatorId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/posts/{postId}/trials", post.getId())
                        .header("X-Demo-User-Id", creatorId)
                        .contentType("application/json")
                        .content("""
                                {"visibility":"PUBLIC","aDisplayName":" A측 ","bDisplayName":" B측 "}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PREPARING"));

        var trial = trialRepository.findAll().stream()
                .filter(candidate -> candidate.getPost().getId().equals(post.getId()))
                .findFirst().orElseThrow();
        assertThat(trial.getStatus()).isEqualTo(TrialStatus.PREPARING);
        assertThat(trial.getVisibility().name()).isEqualTo("PUBLIC");
        assertThat(trialPartyRepository.findByTrialIdOrderBySideAsc(trial.getId()))
                .extracting(party -> party.getSide(), party -> party.getDisplayName(), party -> party.isReady())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(TrialSide.A, "A측", false),
                        org.assertj.core.groups.Tuple.tuple(TrialSide.B, "B측", false));

        mockMvc.perform(post("/api/v1/posts/{postId}/trials", post.getId())
                        .header("X-Demo-User-Id", creatorId)
                        .contentType("application/json")
                        .content("""
                                {"visibility":"PUBLIC","aDisplayName":"A측","bDisplayName":"B측"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TRIAL_ALREADY_EXISTS"));
    }

    @Test
    void rejectsMissingPost() throws Exception {
        mockMvc.perform(post("/api/v1/posts/{postId}/trials", Long.MAX_VALUE)
                        .header("X-Demo-User-Id", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"visibility":"PUBLIC","aDisplayName":"A측","bDisplayName":"B측"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POST_NOT_FOUND"));
    }
}
