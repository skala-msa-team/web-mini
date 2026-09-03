package com.skala.team6.webmini.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 메타 정보")
public record ApiMeta(
        @Schema(example = "2026-09-03T03:00:00Z")
        String timestamp
) {
}
