package com.skala.team6.webmini.trial;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrialPresenceServiceTest {

    @Test
    void broadcastsAudienceCountToTrialEventsAndPresenceTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        TrialPresenceService presenceService = new TrialPresenceService(messagingTemplate);

        int count = presenceService.addSubscriber(1L, "session-1");

        assertThat(count).isEqualTo(1);
        verify(messagingTemplate).convertAndSend(eq("/topic/trials/1/events"), any(Object.class));

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(
                eq("/topic/trials/1/presence"),
                payloadCaptor.capture()
        );
        assertThat(payloadCaptor.getValue())
                .isEqualTo(Map.of("trialId", 1L, "audienceCount", 1));
    }

    @Test
    void broadcastsCurrentAudienceCountOnlyToPresenceTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        TrialPresenceService presenceService = new TrialPresenceService(messagingTemplate);

        presenceService.addSubscriber(1L, "session-1");
        presenceService.broadcastCurrent(1L);

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate, times(2)).convertAndSend(
                eq("/topic/trials/1/presence"),
                payloadCaptor.capture()
        );
        assertThat(payloadCaptor.getAllValues().getLast())
                .isEqualTo(Map.of("trialId", 1L, "audienceCount", 1));
    }
}
