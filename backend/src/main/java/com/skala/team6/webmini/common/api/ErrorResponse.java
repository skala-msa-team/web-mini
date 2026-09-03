package com.skala.team6.webmini.common.api;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "공통 오류 응답")
public record ErrorResponse(
        @Schema(example = "TRIAL_NOT_FOUND")
        String code,
        @Schema(example = "재판을 찾을 수 없습니다.")
        String message,
        @ArraySchema(schema = @Schema(implementation = FieldErrorDetail.class))
        List<FieldErrorDetail> fieldErrors,
        @Schema(example = "2026-09-03T03:00:00Z")
        String timestamp,
        @Schema(example = "/api/v1/trials/10")
        String path
) {
}
