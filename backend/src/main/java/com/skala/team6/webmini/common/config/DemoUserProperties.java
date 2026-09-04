package com.skala.team6.webmini.common.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.demo-user")
public record DemoUserProperties(
        @NotBlank
        String headerName
) {
}
