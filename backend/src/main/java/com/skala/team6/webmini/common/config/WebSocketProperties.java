package com.skala.team6.webmini.common.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.websocket")
public record WebSocketProperties(
        @NotBlank
        String endpoint,
        long[] heartbeat,
        @Min(1)
        int messageSizeLimit,
        @Min(1)
        int sendBufferSizeLimit,
        @Min(1)
        int sendTimeLimit
) {
    public WebSocketProperties {
        if (!endpoint.startsWith("/")) {
            throw new IllegalArgumentException("app.websocket.endpoint must start with '/'.");
        }

        if (heartbeat == null || heartbeat.length != 2 || heartbeat[0] < 0 || heartbeat[1] < 0) {
            throw new IllegalArgumentException("app.websocket.heartbeat must contain two non-negative values.");
        }
    }
}
