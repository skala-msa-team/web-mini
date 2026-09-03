package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.websocket.StompExceptionHandler;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@Validated
public class TrialStompController {

    private final SimpMessagingTemplate messagingTemplate;
    private final StompExceptionHandler stompExceptionHandler;
    private final AtomicLong messageSequence = new AtomicLong(0);

    public TrialStompController(
            SimpMessagingTemplate messagingTemplate,
            StompExceptionHandler stompExceptionHandler
    ) {
        this.messagingTemplate = messagingTemplate;
        this.stompExceptionHandler = stompExceptionHandler;
    }

    @MessageMapping("/trials/{trialId}/chat")
    public void sendChat(
            @DestinationVariable Long trialId,
            @Valid @Payload TrialChatSendRequest request,
            Principal principal
    ) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ApiException(ErrorCode.DEMO_USER_REQUIRED);
        }

        long sequence = messageSequence.incrementAndGet();
        TrialChatMessagePayload payload = new TrialChatMessagePayload(
                sequence,
                sequence,
                trialId,
                new TrialMessageSender(principal.getName(), "Demo-" + principal.getName().substring(0, 8)),
                request.content(),
                Instant.now().toString()
        );

        messagingTemplate.convertAndSend("/topic/trials/" + trialId + "/chat", payload);
    }

    @MessageExceptionHandler(ApiException.class)
    public void handleApiException(ApiException exception, Message<?> message) {
        stompExceptionHandler.handleApiException(exception, message);
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(
            MethodArgumentNotValidException exception,
            Message<?> message
    ) {
        stompExceptionHandler.handleValidationException(exception, message);
    }

    @MessageExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException exception, Message<?> message) {
        stompExceptionHandler.handleConstraintViolationException(exception, message);
    }
}
