package com.skala.team6.webmini.database;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialSpeaker;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.ChatMessageEntity;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialEventEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.entity.VerdictEntity;
import com.skala.team6.webmini.database.repository.ChatMessageRepository;
import com.skala.team6.webmini.database.repository.TrialEventRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.VerdictRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class PostgresPersistenceAcceptanceTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TrialRepository trialRepository;
    @Autowired
    private TrialEventRepository trialEventRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private VerdictRepository verdictRepository;

    @Test
    void storesTrialTimelineAndKeepsChatAndVerdictAfterCompletion() {
        UserEntity user = new UserEntity("acceptance-" + UUID.randomUUID(), "검증 사용자");
        entityManager.persist(user);

        PostEntity post = new PostEntity(
                user,
                "검증 게시글",
                "검증 내용",
                RelationshipType.COUPLE,
                true
        );
        entityManager.persist(post);

        TrialEntity trial = new TrialEntity(post, user);
        OffsetDateTime phaseStartedAt = OffsetDateTime.now().withNano(0);
        OffsetDateTime phaseEndsAt = phaseStartedAt.plusMinutes(5);
        trial.startPhase(TrialStatus.INTRODUCTION, phaseStartedAt, phaseEndsAt);
        entityManager.persist(trial);

        entityManager.persist(new TrialEventEntity(
                trial, 2, "ARGUMENT_PRESENTED", TrialSpeaker.A_LAWYER, "두 번째", "{}"
        ));
        entityManager.persist(new TrialEventEntity(
                trial, 1, "TRIAL_STARTED", TrialSpeaker.SYSTEM, "첫 번째", "{}"
        ));
        ChatMessageEntity secondMessage = new ChatMessageEntity(trial, user, 2, "두 번째 채팅");
        ChatMessageEntity firstMessage = new ChatMessageEntity(trial, user, 1, "첫 번째 채팅");
        entityManager.persist(secondMessage);
        entityManager.persist(firstMessage);

        VerdictEntity verdict = new VerdictEntity(
                trial,
                TrialSide.A,
                40,
                60,
                "A측 승소",
                "[\"검증 근거\"]",
                "대화 시간을 합의합니다.",
                "상대방의 상황을 확인합니다.",
                "judge-v1"
        );
        entityManager.persist(verdict);
        entityManager.flush();
        entityManager.clear();

        TrialEntity storedTrial = trialRepository.findById(trial.getId()).orElseThrow();
        assertThat(storedTrial.getStatus()).isEqualTo(TrialStatus.INTRODUCTION);
        assertThat(storedTrial.getPhaseStartedAt()).isEqualTo(phaseStartedAt);
        assertThat(storedTrial.getPhaseEndsAt()).isEqualTo(phaseEndsAt);

        List<Long> eventSequences = trialEventRepository
                .findByTrialIdOrderBySequenceNoAsc(trial.getId())
                .stream()
                .map(TrialEventEntity::getSequenceNo)
                .toList();
        List<Long> messageSequences = chatMessageRepository
                .findByTrialIdOrderBySequenceNoAsc(trial.getId())
                .stream()
                .map(ChatMessageEntity::getSequenceNo)
                .toList();
        assertThat(eventSequences).containsExactly(1L, 2L);
        assertThat(messageSequences).containsExactly(1L, 2L);

        OffsetDateTime completedAt = phaseEndsAt.plusMinutes(10);
        storedTrial.complete(completedAt);
        entityManager.flush();
        entityManager.clear();

        TrialEntity completedTrial = trialRepository.findById(trial.getId()).orElseThrow();
        assertThat(completedTrial.getStatus()).isEqualTo(TrialStatus.ENDED);
        assertThat(completedTrial.getCompletedAt()).isEqualTo(completedAt);
        assertThat(chatMessageRepository.findByTrialIdOrderBySequenceNoAsc(trial.getId()))
                .extracting(ChatMessageEntity::getId)
                .containsExactly(firstMessage.getId(), secondMessage.getId());
        assertThat(verdictRepository.findByTrialId(trial.getId()))
                .get()
                .extracting(VerdictEntity::getId)
                .isEqualTo(verdict.getId());
    }
}
