package com.skala.team6.webmini.trial;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TrialEventBroadcastListener {
    private final SimpMessagingTemplate messagingTemplate;

    public TrialEventBroadcastListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcast(TrialEventSavedEvent event) {
        TrialEventMessage message = event.message();
        messagingTemplate.convertAndSend(
                "/topic/trials/" + message.trialId() + "/events", message);
    }
}
