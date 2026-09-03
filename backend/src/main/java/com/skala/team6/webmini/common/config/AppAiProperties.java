package com.skala.team6.webmini.common.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.ai")
public record AppAiProperties(
        @NotBlank
        String provider,
        @NotBlank
        String promptVersion
) {
}
