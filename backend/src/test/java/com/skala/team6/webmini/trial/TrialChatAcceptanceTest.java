package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.ChatMessageRepository;
import com.skala.team6.webmini.database.repository.PostRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialChatAcceptanceTest {
    @Autowired
    private TrialChatService trialChatService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TrialRepository trialRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    private TrialEntity trial;

    @BeforeEach
    void setUp() {
        UserEntity creator = userRepository.save(
                new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                creator, "채팅 재판", "내용", RelationshipType.COUPLE, true));
        trial = new TrialEntity(post, creator);
        trial.startPhase(
                TrialStatus.INTRODUCTION,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusMinutes(5)
        );
        trialRepository.save(trial);
    }

    @AfterEach
    void cleanUp() {
        chatMessageRepository.deleteAll();
        trialRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void storesThenBroadcastsChatAfterCommit() {
        String demoUserId = UUID.randomUUID().toString();
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(ignored -> {
            trialChatService.send(trial.getId(), demoUserId, " 저장할 메시지 ");
            verifyNoInteractions(messagingTemplate);
        });

        var stored = chatMessageRepository.findByTrialIdOrderBySequenceNoAsc(trial.getId());
        assertThat(stored).hasSize(1);
        assertThat(stored.get(0).getSequenceNo()).isEqualTo(1);
        assertThat(stored.get(0).getContent()).isEqualTo("저장할 메시지");

        ArgumentCaptor<TrialChatMessagePayload> payloadCaptor =
                ArgumentCaptor.forClass(TrialChatMessagePayload.class);
        verify(messagingTemplate).convertAndSend(
                eq("/topic/trials/" + trial.getId() + "/chat"),
                payloadCaptor.capture()
        );
        TrialChatMessagePayload payload = payloadCaptor.getValue();
        assertThat(payload.messageId()).isEqualTo(stored.get(0).getId());
        assertThat(payload.messageSequence()).isEqualTo(1);
        assertThat(payload.sender().demoUserId()).isEqualTo(demoUserId);
        assertThat(payload.sender().nickname()).isEqualTo("Demo 사용자");
    }

    @Test
    void doesNotBroadcastRolledBackChat() {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        reset(messagingTemplate);

        transaction.executeWithoutResult(status -> {
            trialChatService.send(trial.getId(), UUID.randomUUID().toString(), "롤백 메시지");
            status.setRollbackOnly();
        });

        assertThat(chatMessageRepository.findByTrialIdOrderBySequenceNoAsc(trial.getId())).isEmpty();
        verifyNoInteractions(messagingTemplate);
    }
}
