package com.skala.team6.webmini.websocket;

import com.skala.team6.webmini.common.config.DemoUserProperties;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.demo.DemoUserRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DemoUserChannelInterceptorTest {

    private final DemoUserChannelInterceptor interceptor = new DemoUserChannelInterceptor(
            new DemoUserProperties("X-Demo-User-Id"),
            new DemoUserRegistry()
    );

    @Test
    void bindsPrincipalOnConnect() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        accessor.setNativeHeader("X-Demo-User-Id", "123e4567-e89b-12d3-a456-426614174000");
        accessor.setSessionAttributes(new HashMap<>());

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> intercepted = interceptor.preSend(message, null);
        StompHeaderAccessor interceptedAccessor = MessageHeaderAccessor.getAccessor(intercepted, StompHeaderAccessor.class);

        assertNotNull(interceptedAccessor.getUser());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", interceptedAccessor.getUser().getName());
        assertEquals(
                "123e4567-e89b-12d3-a456-426614174000",
                interceptedAccessor.getSessionAttributes().get(DemoUserChannelInterceptor.DEMO_USER_SESSION_KEY)
        );
    }

    @Test
    void rejectsConnectWhenDemoUserHeaderMissing() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        accessor.setSessionAttributes(new HashMap<>());

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThrows(ApiException.class, () -> interceptor.preSend(message, null));
    }

    @Test
    void restoresPrincipalFromSessionAttributesAfterConnect() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setLeaveMutable(true);
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(DemoUserChannelInterceptor.DEMO_USER_SESSION_KEY, "123e4567-e89b-12d3-a456-426614174000");
        accessor.setSessionAttributes(sessionAttributes);

        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> intercepted = interceptor.preSend(message, null);
        StompHeaderAccessor interceptedAccessor = MessageHeaderAccessor.getAccessor(intercepted, StompHeaderAccessor.class);

        assertNotNull(interceptedAccessor.getUser());
        assertEquals("123e4567-e89b-12d3-a456-426614174000", interceptedAccessor.getUser().getName());
    }
}
