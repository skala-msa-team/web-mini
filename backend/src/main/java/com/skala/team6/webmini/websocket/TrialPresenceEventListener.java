package com.skala.team6.webmini.websocket;

import com.skala.team6.webmini.trial.TrialPresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TrialPresenceEventListener implements
        ApplicationListener<SessionSubscribeEvent> {

    private static final Logger log = LoggerFactory.getLogger(TrialPresenceEventListener.class);
    private static final Pattern TRIAL_EVENTS_TOPIC = Pattern.compile("/topic/trials/(\\d+)/events");
    private static final Pattern TRIAL_PRESENCE_TOPIC = Pattern.compile("/topic/trials/(\\d+)/presence");

    private final TrialPresenceService presenceService;

    public TrialPresenceEventListener(TrialPresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        if (destination == null)
            return;
        Matcher eventsTopicMatcher = TRIAL_EVENTS_TOPIC.matcher(destination);
        if (eventsTopicMatcher.find()) {
            Long trialId = Long.valueOf(eventsTopicMatcher.group(1));
            int count = presenceService.addSubscriber(trialId, sessionId);
            log.debug("Added subscriber {} to trial {} (count={})", sessionId, trialId, count);
            return;
        }

        Matcher presenceTopicMatcher = TRIAL_PRESENCE_TOPIC.matcher(destination);
        if (presenceTopicMatcher.find()) {
            Long trialId = Long.valueOf(presenceTopicMatcher.group(1));
            presenceService.broadcastCurrent(trialId);
        }
    }

    // Handle unsubscribe and disconnect in a best-effort way by listening to these
    // events
    @org.springframework.context.event.EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = accessor.getSessionId();
            // destination may not be present on unsubscribe; best-effort: remove from all
            // trials
            // Not removing here to avoid complexity; disconnect handler will clean up.
        } catch (Exception ignored) {
        }
    }

    @org.springframework.context.event.EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        try {
            String sessionId = event.getSessionId();
            // best-effort: remove sessionId from all tracked trials
            // presenceService holds map; iterate keys
            var field = presenceService.getClass().getDeclaredField("presence");
            field.setAccessible(true);
            var map = (java.util.Map<Long, java.util.Set<String>>) field.get(presenceService);
            for (var entry : map.entrySet()) {
                if (entry.getValue().remove(sessionId)) {
                    presenceService.removeSubscriber(entry.getKey(), sessionId);
                }
            }
        } catch (Exception ex) {
            log.debug("Failed to cleanup disconnect: {}", ex.getMessage());
        }
    }
}
