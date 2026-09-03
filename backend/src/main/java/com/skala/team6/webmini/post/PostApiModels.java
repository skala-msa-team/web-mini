package com.skala.team6.webmini.post;

import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record CreatePostRequest(
        @Schema(example = "연락 문제로 다툰 사연")
        @NotBlank
        @Size(min = 1, max = 150)
        String title,
        @Schema(example = "연인이 답장이 늦어 갈등이 생겼습니다.")
        @NotBlank
        @Size(min = 1, max = 5000)
        String content,
        @NotNull
        RelationshipType relationshipType,
        boolean trialRequested
) {
}

record CreatePostResponse(
        @Schema(example = "10")
        Long postId,
        String title,
        String content,
        RelationshipType relationshipType,
        boolean trialRequested
) {
}

record CreateTrialRequest(
        @NotNull
        Visibility visibility,
        @Schema(example = "A측")
        @NotBlank
        String aDisplayName,
        @Schema(example = "B측")
        @NotBlank
        String bDisplayName
) {
}

record CreateTrialResponse(
        @Schema(example = "21")
        Long trialId,
        TrialStatus status,
        TrialPartySummary aParty,
        TrialPartySummary bParty
) {
}

record TrialPartySummary(
        String side,
        String displayName
) {
}
