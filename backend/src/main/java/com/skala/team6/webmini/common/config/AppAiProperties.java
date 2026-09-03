package com.skala.team6.webmini.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai")
public record AppAiProperties(
        String provider,
        String promptVersion
) {
}
