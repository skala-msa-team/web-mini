package com.skala.team6.webmini.websocket;

public record StompErrorPayload(
        String code,
        String message,
        Long trialId,
        String destination,
        String occurredAt
) {
}
