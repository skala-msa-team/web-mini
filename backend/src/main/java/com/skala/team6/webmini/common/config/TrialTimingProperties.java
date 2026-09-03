package com.skala.team6.webmini.common.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.trial")
public record TrialTimingProperties(
        @Min(1) long introductionSeconds,
        @Min(1) long argumentSeconds,
        @Min(1) long debateSeconds,
        @Min(1) long votingSeconds
) {
}
