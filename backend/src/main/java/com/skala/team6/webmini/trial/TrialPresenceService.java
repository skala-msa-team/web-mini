package com.skala.team6.webmini.trial;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrialPresenceService {
    private final Map<Long, Set<String>> presence = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public TrialPresenceService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public int addSubscriber(Long trialId, String sessionId) {
        var set = presence.computeIfAbsent(trialId, k -> ConcurrentHashMap.newKeySet());
        set.add(sessionId);
        int count = set.size();
        broadcast(trialId, count);
        return count;
    }

    public int removeSubscriber(Long trialId, String sessionId) {
        var set = presence.get(trialId);
        if (set == null)
            return 0;
        set.remove(sessionId);
        int count = set.size();
        broadcast(trialId, count);
        return count;
    }

    private void broadcast(Long trialId, int count) {
        var payload = Map.of("trialId", trialId, "audienceCount", count);
        var event = Map.of(
                "type", "PRESENCE_UPDATED",
                "sequence", null,
                "payload", payload);
        messagingTemplate.convertAndSend(String.format("/topic/trials/%d/events", trialId), (Object) event);
    }
}
