package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.ChatMessageEntity;
import com.skala.team6.webmini.database.entity.TrialEventEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.ChatMessageRepository;
import com.skala.team6.webmini.database.repository.TrialEventRepository;
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

import java.time.OffsetDateTime;
import java.util.UUID;

import com.skala.team6.webmini.common.model.TrialSpeaker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialRecoveryAcceptanceTest {
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
    private TrialEventRepository trialEventRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    void listsOnlyPublicActiveTrialsWithPagingAndStatusFilter() throws Exception {
        createTrial("준비 중", TrialStatus.PREPARING);
        TrialEntity introduction = createTrial("진행 중", TrialStatus.INTRODUCTION);
        createTrial("종료됨", TrialStatus.ENDED);

        mockMvc.perform(get("/api/v1/trials").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].trialId").value(introduction.getId()))
                .andExpect(jsonPath("$.data.items[0].status").value("INTRODUCTION"))
                .andExpect(jsonPath("$.data.items[0].title").value("진행 중"))
                .andExpect(jsonPath("$.data.items[0].aDisplayName").value("A측"))
                .andExpect(jsonPath("$.data.items[0].bDisplayName").value("B측"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));

        mockMvc.perform(get("/api/v1/trials").param("status", "A_ARGUMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty());

        mockMvc.perform(get("/api/v1/trials").param("status", "PREPARING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void returnsStoredTrialDetail() throws Exception {
        TrialEntity trial = createTrial("상세 재판", TrialStatus.A_ARGUMENT);

        mockMvc.perform(get("/api/v1/trials/{trialId}", trial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.trialId").value(trial.getId()))
                .andExpect(jsonPath("$.data.title").value("상세 재판"))
                .andExpect(jsonPath("$.data.status").value("A_ARGUMENT"))
                .andExpect(jsonPath("$.data.aParty.side").value("A"))
                .andExpect(jsonPath("$.data.bParty.side").value("B"));
    }

    @Test
    void returnsSnapshotFromStoredStateAndLatestSequences() throws Exception {
        TrialEntity trial = createTrial("스냅샷 재판", TrialStatus.VOTING);
        OffsetDateTime scheduledEndAt = OffsetDateTime.now().plusMinutes(20);
        trial.scheduleEnd(scheduledEndAt);
        trialRepository.save(trial);
        UserEntity sender = userRepository.save(
                new UserEntity(UUID.randomUUID().toString(), "관전자"));
        trialEventRepository.save(new TrialEventEntity(
                trial, 1, "TRIAL_STARTED", TrialSpeaker.SYSTEM, null, "{}"));
        trialEventRepository.save(new TrialEventEntity(
                trial, 3, "VOTING_OPENED", TrialSpeaker.SYSTEM, null, "{}"));
        chatMessageRepository.save(new ChatMessageEntity(trial, sender, 7, "메시지"));

        mockMvc.perform(get("/api/v1/trials/{trialId}/snapshot", trial.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("VOTING"))
                .andExpect(jsonPath("$.data.phaseStartedAt").isNotEmpty())
                .andExpect(jsonPath("$.data.phaseEndsAt").isNotEmpty())
                .andExpect(jsonPath("$.data.scheduledEndAt").value(scheduledEndAt.toString()))
                .andExpect(jsonPath("$.data.latestEventSequence").value(3))
                .andExpect(jsonPath("$.data.latestMessageSequence").value(7))
                .andExpect(jsonPath("$.data.voteOpen").value(true))
                .andExpect(jsonPath("$.data.ended").value(false));
    }

    private TrialEntity createTrial(String title, TrialStatus status) {
        UserEntity user = userRepository.save(
                new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                user, title, "내용", RelationshipType.COUPLE, true));
        TrialEntity trial = trialRepository.save(new TrialEntity(post, user));
        if (status == TrialStatus.ENDED) {
            trial.complete(OffsetDateTime.now());
        } else if (status != TrialStatus.PREPARING) {
            trial.startPhase(status, OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(5));
        }
        trialRepository.save(trial);
        trialPartyRepository.save(new TrialPartyEntity(trial, TrialSide.A, "A측"));
        trialPartyRepository.save(new TrialPartyEntity(trial, TrialSide.B, "B측"));
        return trial;
    }
}
