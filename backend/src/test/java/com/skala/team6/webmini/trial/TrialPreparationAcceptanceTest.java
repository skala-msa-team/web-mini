package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialPreparationAcceptanceTest {
    private static final String DEMO_USER_ID = "11111111-1111-4111-8111-111111111111";

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
    @Autowired
    private TrialStatementRepository trialStatementRepository;
    @Autowired
    private EntityManager entityManager;

    private TrialEntity trial;
    private TrialPartyEntity aParty;
    private TrialPartyEntity bParty;

    @BeforeEach
    void setUp() {
        UserEntity user = userRepository.save(new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                user, "게시글", "내용", RelationshipType.COUPLE, true));
        trial = trialRepository.save(new TrialEntity(post, user));
        aParty = trialPartyRepository.save(new TrialPartyEntity(trial, TrialSide.A, "A측"));
        bParty = trialPartyRepository.save(new TrialPartyEntity(trial, TrialSide.B, "B측"));
    }

    @Test
    void savesAndUpdatesStatementsWithoutOverwritingOtherSide() throws Exception {
        saveStatement(TrialSide.A, "A측 최초 상황")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.side").value("A"))
                .andExpect(jsonPath("$.data.statement.situation").value("A측 최초 상황"));
        saveStatement(TrialSide.B, "B측 상황").andExpect(status().isOk());
        saveStatement(TrialSide.A, "A측 수정 상황").andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        assertThat(trialStatementRepository.findByTrialPartyId(aParty.getId()).orElseThrow().getSituation())
                .isEqualTo("A측 수정 상황");
        assertThat(trialStatementRepository.findByTrialPartyId(bParty.getId()).orElseThrow().getSituation())
                .isEqualTo("B측 상황");
    }

    @Test
    void rejectsMissingTrialInvalidSideAndMissingDemoUser() throws Exception {
        mockMvc.perform(put("/api/v1/trials/{trialId}/parties/A/statement", Long.MAX_VALUE)
                        .header("X-Demo-User-Id", DEMO_USER_ID)
                        .contentType("application/json")
                        .content(statementRequest("상황")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRIAL_NOT_FOUND"));

        mockMvc.perform(put("/api/v1/trials/{trialId}/parties/C/statement", trial.getId())
                        .header("X-Demo-User-Id", DEMO_USER_ID)
                        .contentType("application/json")
                        .content(statementRequest("상황")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/trials/{trialId}/parties/A/statement", trial.getId())
                        .contentType("application/json")
                        .content(statementRequest("상황")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DEMO_USER_REQUIRED"));
    }

    private org.springframework.test.web.servlet.ResultActions saveStatement(
            TrialSide side,
            String situation
    ) throws Exception {
        return mockMvc.perform(put("/api/v1/trials/{trialId}/parties/{side}/statement", trial.getId(), side)
                .header("X-Demo-User-Id", DEMO_USER_ID)
                .contentType("application/json")
                .content(statementRequest(situation)));
    }

    private String statementRequest(String situation) {
        return """
                {
                  "incidentTime": "어제 저녁",
                  "situation": "%s",
                  "counterpartAction": "상대 행동",
                  "ownAction": "내 행동",
                  "afterConversation": "이후 대화",
                  "desiredResolution": "원하는 해결"
                }
                """.formatted(situation);
    }
}
