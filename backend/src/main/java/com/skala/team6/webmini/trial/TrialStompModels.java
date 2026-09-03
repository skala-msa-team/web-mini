package com.skala.team6.webmini.trial;

import jakarta.validation.constraints.NotBlank;

record TrialChatSendRequest(
        @NotBlank(message = "채팅 내용이 필요합니다.")
        String content
) {
}

record TrialChatMessagePayload(
        Long messageId,
        long messageSequence,
        Long trialId,
        TrialMessageSender sender,
        String content,
        String createdAt
) {
}
