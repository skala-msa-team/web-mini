package com.skala.team6.webmini.common.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "공통 성공 응답")
public record ApiResponse<T>(
        T data,
        ApiMeta meta
) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, new ApiMeta(Instant.now().toString()));
    }
}
