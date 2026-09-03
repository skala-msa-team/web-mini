package com.skala.team6.webmini.trial;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TrialChatBroadcastListener {
    private final SimpMessagingTemplate messagingTemplate;

    public TrialChatBroadcastListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcast(TrialChatSavedEvent event) {
        TrialChatMessagePayload payload = event.payload();
        messagingTemplate.convertAndSend(
                "/topic/trials/" + payload.trialId() + "/chat",
                payload
        );
    }
}
