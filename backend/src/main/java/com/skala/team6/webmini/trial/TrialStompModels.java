package com.skala.team6.webmini.trial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record TrialChatSendRequest(
        @NotBlank(message = "채팅 내용이 필요합니다.")
        @Size(max = 1000, message = "채팅 글자 수 제한을 초과했습니다.")
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
