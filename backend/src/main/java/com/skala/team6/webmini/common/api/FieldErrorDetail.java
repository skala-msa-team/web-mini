package com.skala.team6.webmini.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "필드 검증 오류")
public record FieldErrorDetail(
        @Schema(example = "title")
        String field,
        @Schema(example = "must not be blank")
        String reason
) {
}
