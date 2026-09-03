package com.skala.team6.webmini.websocket;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StompExceptionHandlerTest {

    @Test
    void sendsErrorPayloadToTheOriginatingUsersQueue() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        StompExceptionHandler handler = new StompExceptionHandler(messagingTemplate);
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setUser((Principal) () -> "123e4567-e89b-12d3-a456-426614174000");
        accessor.setDestination("/app/trials/1/chat");
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        handler.handleApiException(new ApiException(ErrorCode.DEMO_USER_REQUIRED), message);

        ArgumentCaptor<StompErrorPayload> payloadCaptor = ArgumentCaptor.forClass(StompErrorPayload.class);
        verify(messagingTemplate).convertAndSendToUser(
                eq("123e4567-e89b-12d3-a456-426614174000"),
                eq("/queue/errors"),
                payloadCaptor.capture()
        );
        assertEquals(ErrorCode.DEMO_USER_REQUIRED.name(), payloadCaptor.getValue().code());
        assertEquals(1L, payloadCaptor.getValue().trialId());
    }

    @Test
    void fallsBackToDemoUserIdStoredInTheSession() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        StompExceptionHandler handler = new StompExceptionHandler(messagingTemplate);
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setSessionAttributes(new HashMap<>());
        accessor.getSessionAttributes().put(
                DemoUserChannelInterceptor.DEMO_USER_SESSION_KEY,
                "123e4567-e89b-12d3-a456-426614174001"
        );
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        handler.handleApiException(new ApiException(ErrorCode.DEMO_USER_REQUIRED), message);

        verify(messagingTemplate).convertAndSendToUser(
                eq("123e4567-e89b-12d3-a456-426614174001"),
                eq("/queue/errors"),
                org.mockito.ArgumentMatchers.any(StompErrorPayload.class)
        );
    }
}
