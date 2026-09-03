package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.TrialStatementEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.TrialEventRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import com.skala.team6.webmini.database.repository.VerdictRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.trial.scheduler-enabled=false")
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialProgressAcceptanceTest {
    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired TrialRepository trialRepository;
    @Autowired TrialPartyRepository trialPartyRepository;
    @Autowired TrialEventRepository trialEventRepository;
    @Autowired TrialStatementRepository trialStatementRepository;
    @Autowired VerdictRepository verdictRepository;
    @Autowired TrialPhaseService trialPhaseService;
    @MockitoBean SimpMessagingTemplate messagingTemplate;

    @AfterEach
    void cleanUp() {
        trialEventRepository.deleteAll();
        verdictRepository.deleteAll();
        trialStatementRepository.deleteAll();
        trialPartyRepository.deleteAll();
        trialRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void startsReadyTrialAndPublishesPersistedEventsInSequence() throws Exception {
        UserEntity user = userRepository.save(new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                user, "재판 제목", "재판 내용", RelationshipType.COUPLE, true));
        TrialEntity trial = trialRepository.save(new TrialEntity(post, user));
        TrialPartyEntity a = new TrialPartyEntity(trial, TrialSide.A, "A측");
        TrialPartyEntity b = new TrialPartyEntity(trial, TrialSide.B, "B측");
        a.markReady();
        b.markReady();
        trialPartyRepository.save(a);
        trialPartyRepository.save(b);

        mockMvc.perform(post("/api/v1/trials/{trialId}/start", trial.getId())
                        .header("X-Demo-User-Id", user.getDemoKey()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INTRODUCTION"))
                .andExpect(jsonPath("$.data.phaseStartedAt").isNotEmpty())
                .andExpect(jsonPath("$.data.phaseEndsAt").isNotEmpty())
                .andExpect(jsonPath("$.data.latestEventSequence").value(2));

        TrialEntity started = trialRepository.findById(trial.getId()).orElseThrow();
        assertThat(started.getStatus()).isEqualTo(TrialStatus.INTRODUCTION);
        assertThat(started.getPhaseStartedAt()).isNotNull();
        assertThat(started.getPhaseEndsAt()).isNotNull();
        assertThat(started.getScheduledEndAt()).isNotNull();
        assertThat(trialEventRepository.findByTrialIdOrderBySequenceNoAsc(trial.getId()))
                .extracting("sequenceNo", "eventType")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1L, "TRIAL_STARTED"),
                        org.assertj.core.groups.Tuple.tuple(2L, "JUDGE_INTRODUCTION"));
        verify(messagingTemplate, timeout(1000).times(2))
                .convertAndSend(eq("/topic/trials/" + trial.getId() + "/events"),
                        org.mockito.ArgumentMatchers.any(TrialEventMessage.class));
    }

    @Test
    void advancesAllPhasesAndPersistsArgumentsVerdictAndEndEvents() {
        UserEntity user = userRepository.save(new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                user, "재판 제목", "재판 내용", RelationshipType.COUPLE, true));
        TrialEntity trial = trialRepository.save(new TrialEntity(post, user));
        readyParty(trial, TrialSide.A, "A측", "A측 최종 변론");
        readyParty(trial, TrialSide.B, "B측", "B측 최종 변론");

        // 시작 이후 각 phaseEndsAt을 기준으로 스케줄러와 동일한 전이를 직접 실행한다.
        startService.start(trial.getId());
        for (int i = 0; i < 6; i++) {
            TrialEntity current = trialRepository.findById(trial.getId()).orElseThrow();
            trialPhaseService.advanceIfExpired(trial.getId(), current.getPhaseEndsAt());
        }

        TrialEntity ended = trialRepository.findById(trial.getId()).orElseThrow();
        assertThat(ended.getStatus()).isEqualTo(TrialStatus.ENDED);
        assertThat(verdictRepository.findByTrialId(trial.getId())).isPresent();
        var events = trialEventRepository.findByTrialIdOrderBySequenceNoAsc(trial.getId());
        assertThat(events).extracting("sequenceNo", "eventType")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1L, "TRIAL_STARTED"),
                        org.assertj.core.groups.Tuple.tuple(2L, "JUDGE_INTRODUCTION"),
                        org.assertj.core.groups.Tuple.tuple(3L, "A_ARGUMENT"),
                        org.assertj.core.groups.Tuple.tuple(4L, "B_ARGUMENT"),
                        org.assertj.core.groups.Tuple.tuple(5L, "DEBATE_STARTED"),
                        org.assertj.core.groups.Tuple.tuple(6L, "A_DEBATE"),
                        org.assertj.core.groups.Tuple.tuple(7L, "B_DEBATE"),
                        org.assertj.core.groups.Tuple.tuple(8L, "A_DEBATE"),
                        org.assertj.core.groups.Tuple.tuple(9L, "B_DEBATE"),
                        org.assertj.core.groups.Tuple.tuple(10L, "A_DEBATE"),
                        org.assertj.core.groups.Tuple.tuple(11L, "B_DEBATE"),
                        org.assertj.core.groups.Tuple.tuple(12L, "VOTING_STARTED"),
                        org.assertj.core.groups.Tuple.tuple(13L, "VERDICT_ANNOUNCED"),
                        org.assertj.core.groups.Tuple.tuple(14L, "TRIAL_ENDED"));
        assertThat(events).filteredOn(event -> event.getEventType().endsWith("_DEBATE"))
                .allSatisfy(event -> assertThat(event.getContent()).isNotBlank());
    }

    @Autowired TrialStartService startService;

    private TrialPartyEntity readyParty(TrialEntity trial, TrialSide side,
                                        String displayName, String argument) {
        TrialPartyEntity party = new TrialPartyEntity(trial, side, displayName);
        party.markReady();
        trialPartyRepository.save(party);
        TrialStatementEntity statement = new TrialStatementEntity(
                party, "어제", "상황", "상대 행동", "내 행동", "대화", "해결");
        statement.updateArgumentDraft("사실", argument);
        trialStatementRepository.save(statement);
        return party;
    }
}
