package com.skala.team6.webmini.websocket;

import com.skala.team6.webmini.common.config.DemoUserProperties;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.demo.DemoUserRegistry;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
public class DemoUserChannelInterceptor implements ChannelInterceptor {

    public static final String DEMO_USER_SESSION_KEY = "demoUserId";

    private final DemoUserProperties demoUserProperties;
    private final DemoUserRegistry demoUserRegistry;

    public DemoUserChannelInterceptor(
            DemoUserProperties demoUserProperties,
            DemoUserRegistry demoUserRegistry) {
        this.demoUserProperties = demoUserProperties;
        this.demoUserRegistry = demoUserRegistry;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(command)) {
            String demoUserId = resolveDemoUserId(accessor);
            demoUserRegistry.getOrCreate(demoUserId);
            accessor.setUser(new DemoUserPrincipal(demoUserId));
            sessionAttributes(accessor).put(DEMO_USER_SESSION_KEY, demoUserId);
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        Principal user = accessor.getUser();
        if (user == null) {
            String demoUserId = (String) sessionAttributes(accessor).get(DEMO_USER_SESSION_KEY);
            if (demoUserId == null || demoUserId.isBlank()) {
                throw new ApiException(ErrorCode.DEMO_USER_REQUIRED);
            }
            accessor.setUser(new DemoUserPrincipal(demoUserId));
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private String resolveDemoUserId(StompHeaderAccessor accessor) {
        String demoUserId = accessor.getFirstNativeHeader(demoUserProperties.headerName());
        if (isValidUuid(demoUserId)) {
            return demoUserId;
        }

        String sessionId = accessor.getSessionId();
        if (sessionId == null || sessionId.isBlank()) {
            throw new ApiException(ErrorCode.DEMO_USER_REQUIRED);
        }

        return "ws-" + sessionId;
    }

    private boolean isValidUuid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return value
                .matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    }

    private Map<String, Object> sessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new ApiException(ErrorCode.DEMO_USER_REQUIRED);
        }
        return sessionAttributes;
    }
}
