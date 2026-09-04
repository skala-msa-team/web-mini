package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.TrialSpeaker;

import java.time.OffsetDateTime;
import java.util.Map;

public record TrialEventMessage(Long eventId, Long trialId, long sequence, String type,
                                TrialSpeaker speaker, String content, OffsetDateTime occurredAt,
                                Map<String, Object> payload) {
}
