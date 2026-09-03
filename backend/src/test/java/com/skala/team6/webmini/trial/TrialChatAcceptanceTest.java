package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void restoresMessagesAfterCursorAndKeepsHistoryAfterTrialEnds() throws Exception {
        String demoUserId = UUID.randomUUID().toString();
        trialChatService.send(trial.getId(), demoUserId, "첫 메시지");
        trialChatService.send(trial.getId(), demoUserId, "둘째 메시지");
        trialChatService.send(trial.getId(), demoUserId, "셋째 메시지");

        mockMvc.perform(get("/api/v1/trials/{trialId}/messages", trial.getId())
                        .param("afterSequence", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].messageSequence").value(2))
                .andExpect(jsonPath("$.data.items[0].messageId").isNumber())
                .andExpect(jsonPath("$.data.items[0].sender.demoUserId").value(demoUserId))
                .andExpect(jsonPath("$.data.latestMessageSequence").value(2))
                .andExpect(jsonPath("$.data.hasMore").value(true));

        trial.complete(OffsetDateTime.now());
        trialRepository.saveAndFlush(trial);

        mockMvc.perform(get("/api/v1/trials/{trialId}/messages", trial.getId())
                        .param("afterSequence", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].messageSequence").value(3))
                .andExpect(jsonPath("$.data.items[0].content").value("셋째 메시지"))
                .andExpect(jsonPath("$.data.latestMessageSequence").value(3))
                .andExpect(jsonPath("$.data.hasMore").value(false));
    }

    @Test
    void rejectsInactiveTrialMissingTrialAndInvalidContent() {
        trial.complete(OffsetDateTime.now());
        trialRepository.saveAndFlush(trial);

        assertApiError(
                () -> trialChatService.send(
                        trial.getId(), UUID.randomUUID().toString(), "종료 후 메시지"),
                ErrorCode.CHAT_NOT_ALLOWED
        );
        assertApiError(
                () -> trialChatService.send(
                        Long.MAX_VALUE, UUID.randomUUID().toString(), "메시지"),
                ErrorCode.TRIAL_NOT_FOUND
        );
        TrialEntity activeTrial = createActiveTrial("글자 수 재판");
        assertApiError(
                () -> trialChatService.send(
                        activeTrial.getId(), UUID.randomUUID().toString(), " "),
                ErrorCode.VALIDATION_ERROR
        );
        assertApiError(
                () -> trialChatService.send(
                        activeTrial.getId(), UUID.randomUUID().toString(), "x".repeat(501)),
                ErrorCode.MESSAGE_TOO_LONG
        );
    }

    @Test
    void assignsIndependentSequencesPerTrial() {
        String demoUserId = UUID.randomUUID().toString();
        TrialChatMessagePayload first = trialChatService.send(
                trial.getId(), demoUserId, "첫 재판 첫 메시지");
        trialChatService.send(trial.getId(), demoUserId, "첫 재판 둘째 메시지");
        TrialEntity otherTrial = createActiveTrial("다른 재판");
        TrialChatMessagePayload otherFirst = trialChatService.send(
                otherTrial.getId(), demoUserId, "다른 재판 첫 메시지");

        assertThat(first.messageSequence()).isEqualTo(1);
        assertThat(otherFirst.messageSequence()).isEqualTo(1);
    }

    @Test
    void validatesHistoryCursorAndSize() throws Exception {
        mockMvc.perform(get("/api/v1/trials/{trialId}/messages", trial.getId())
                        .param("afterSequence", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(get("/api/v1/trials/{trialId}/messages", trial.getId())
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(get("/api/v1/trials/{trialId}/messages", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRIAL_NOT_FOUND"));
    }

    private TrialEntity createActiveTrial(String title) {
        UserEntity creator = userRepository.save(
                new UserEntity(UUID.randomUUID().toString(), "다른 작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                creator, title, "내용", RelationshipType.COUPLE, true));
        TrialEntity activeTrial = new TrialEntity(post, creator);
        activeTrial.startPhase(
                TrialStatus.INTRODUCTION,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusMinutes(5)
        );
        return trialRepository.save(activeTrial);
    }

    private void assertApiError(Runnable action, ErrorCode errorCode) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(errorCode));
    }
}
