package com.skala.team6.webmini.websocket;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class StompExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public StompExceptionHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageExceptionHandler(ApiException.class)
    public void handleApiException(ApiException exception, Message<?> message) {
        sendToUser(toPayload(exception.getErrorCode(), exception.getMessage(), message), message);
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(
            MethodArgumentNotValidException exception,
            Message<?> message
    ) {
        FieldError firstError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String messageText = firstError != null ? firstError.getDefaultMessage() : ErrorCode.VALIDATION_ERROR.message();
        sendToUser(toPayload(ErrorCode.VALIDATION_ERROR, messageText, message), message);
    }

    @MessageExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(
            ConstraintViolationException exception,
            Message<?> message
    ) {
        String messageText = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse(ErrorCode.VALIDATION_ERROR.message());
        sendToUser(toPayload(ErrorCode.VALIDATION_ERROR, messageText, message), message);
    }

    @MessageExceptionHandler(Exception.class)
    public void handleUnhandledException(Exception exception, Message<?> message) {
        sendToUser(toPayload(ErrorCode.VALIDATION_ERROR, exception.getMessage(), message), message);
    }

    private void sendToUser(StompErrorPayload payload, Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String demoUserId = accessor.getUser() != null
                ? accessor.getUser().getName()
                : sessionAttributes == null
                ? null
                : (String) sessionAttributes.get(DemoUserChannelInterceptor.DEMO_USER_SESSION_KEY);
        if (demoUserId != null && !demoUserId.isBlank()) {
            messagingTemplate.convertAndSendToUser(demoUserId, "/queue/errors", payload);
        }
    }

    private StompErrorPayload toPayload(ErrorCode errorCode, String messageText, Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        return new StompErrorPayload(
                errorCode.name(),
                messageText,
                extractTrialId(accessor.getDestination()),
                accessor.getDestination(),
                Instant.now().toString()
        );
    }

    private Long extractTrialId(String destination) {
        if (destination == null) {
            return null;
        }

        String[] parts = destination.split("/");
        for (int index = 0; index < parts.length; index++) {
            if ("trials".equals(parts[index]) && index + 1 < parts.length) {
                try {
                    return Long.valueOf(parts[index + 1]);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }

        return null;
    }
}
