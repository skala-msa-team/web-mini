package com.skala.team6.webmini.trial;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
        if (set == null) {
            return 0;
        }
        set.remove(sessionId);
        int count = set.size();
        broadcast(trialId, count);
        return count;
    }

    public int getAudienceCount(Long trialId) {
        var set = presence.get(trialId);
        return set == null ? 0 : set.size();
    }

    public void broadcastCurrent(Long trialId) {
        broadcastPresence(trialId, getAudienceCount(trialId));
    }

    private void broadcast(Long trialId, int count) {
        var payload = Map.of("trialId", trialId, "audienceCount", count);
        var event = new HashMap<String, Object>();
        event.put("type", "PRESENCE_UPDATED");
        event.put("sequence", null);
        event.put("payload", payload);
        messagingTemplate.convertAndSend(String.format("/topic/trials/%d/events", trialId), (Object) event);
        broadcastPresence(trialId, count);
    }

    private void broadcastPresence(Long trialId, int count) {
        var payload = Map.of("trialId", trialId, "audienceCount", count);
        messagingTemplate.convertAndSend(String.format("/topic/trials/%d/presence", trialId), payload);
    }
}
