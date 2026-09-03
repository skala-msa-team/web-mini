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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class TrialChatWebSocketIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TrialRepository trialRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    @Qualifier("clientInboundChannel")
    private AbstractMessageChannel clientInboundChannel;

    private WebSocketStompClient stompClient;
    private StompSession firstSession;
    private StompSession secondSession;
    private TrialEntity trial;
    private CountDownLatch subscriptionLatch;
    private ChannelInterceptor subscriptionInterceptor;

    @BeforeEach
    void setUp() {
        UserEntity creator = userRepository.save(
                new UserEntity(UUID.randomUUID().toString(), "작성자"));
        PostEntity post = postRepository.save(new PostEntity(
                creator, "STOMP 동기화 재판", "내용", RelationshipType.COUPLE, true));
        trial = new TrialEntity(post, creator);
        trial.startPhase(
                TrialStatus.INTRODUCTION,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusMinutes(5)
        );
        trialRepository.save(trial);

        subscriptionLatch = new CountDownLatch(2);
        subscriptionInterceptor = new ChannelInterceptor() {
            @Override
            public void afterSendCompletion(
                    Message<?> message,
                    MessageChannel channel,
                    boolean sent,
                    Exception exception
            ) {
                if (StompCommand.SUBSCRIBE.equals(StompHeaderAccessor.wrap(message).getCommand())) {
                    subscriptionLatch.countDown();
                }
            }
        };
        clientInboundChannel.addInterceptor(subscriptionInterceptor);

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @AfterEach
    void cleanUp() {
        if (subscriptionInterceptor != null) {
            clientInboundChannel.removeInterceptor(subscriptionInterceptor);
        }
        if (firstSession != null && firstSession.isConnected()) {
            firstSession.disconnect();
        }
        if (secondSession != null && secondSession.isConnected()) {
            secondSession.disconnect();
        }
        if (stompClient != null) {
            stompClient.stop();
        }
        chatMessageRepository.deleteAll();
        trialRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void synchronizesOneSavedMessageToTwoConnectedClients() throws Exception {
        firstSession = connect(UUID.randomUUID().toString());
        secondSession = connect(UUID.randomUUID().toString());
        String destination = "/topic/trials/" + trial.getId() + "/chat";
        CompletableFuture<TrialChatMessagePayload> firstMessage = subscribe(firstSession, destination);
        CompletableFuture<TrialChatMessagePayload> secondMessage = subscribe(secondSession, destination);
        assertThat(subscriptionLatch.await(5, TimeUnit.SECONDS)).isTrue();

        firstSession.send(
                "/app/trials/" + trial.getId() + "/chat",
                Map.of("content", "두 클라이언트 동기화")
        );

        TrialChatMessagePayload receivedByFirst = firstMessage.get(5, TimeUnit.SECONDS);
        TrialChatMessagePayload receivedBySecond = secondMessage.get(5, TimeUnit.SECONDS);
        assertThat(receivedBySecond).isEqualTo(receivedByFirst);
        assertThat(receivedByFirst.messageSequence()).isEqualTo(1);
        assertThat(receivedByFirst.content()).isEqualTo("두 클라이언트 동기화");
        assertThat(chatMessageRepository.findByTrialIdOrderBySequenceNoAsc(trial.getId()))
                .singleElement()
                .satisfies(stored -> assertThat(stored.getId()).isEqualTo(receivedByFirst.messageId()));
    }

    private StompSession connect(String demoUserId) throws Exception {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("X-Demo-User-Id", demoUserId);
        return stompClient.connectAsync(
                "ws://127.0.0.1:" + port + "/ws",
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                }
        ).get(5, TimeUnit.SECONDS);
    }

    private CompletableFuture<TrialChatMessagePayload> subscribe(
            StompSession session,
            String destination
    ) {
        CompletableFuture<TrialChatMessagePayload> message = new CompletableFuture<>();
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return TrialChatMessagePayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                message.complete((TrialChatMessagePayload) payload);
            }
        });
        return message;
    }

}
