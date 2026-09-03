package com.skala.team6.webmini.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.demo-user")
public record DemoUserProperties(
        String headerName
) {
}
