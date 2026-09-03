package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;
import com.skala.team6.webmini.database.entity.ChatMessageEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.ChatMessageRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrialChatQueryService {
    private final TrialRepository trialRepository;
    private final ChatMessageRepository chatMessageRepository;

    public TrialChatQueryService(
            TrialRepository trialRepository,
            ChatMessageRepository chatMessageRepository
    ) {
        this.trialRepository = trialRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(readOnly = true)
    public TrialMessagesResponse findMessages(Long trialId, long afterSequence, int size) {
        trialRepository.findByIdAndVisibilityAndStatusNot(
                        trialId, Visibility.PUBLIC, TrialStatus.PREPARING)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));

        List<ChatMessageEntity> fetched = chatMessageRepository
                .findByTrialIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
                        trialId,
                        afterSequence,
                        PageRequest.of(0, size + 1)
                );
        boolean hasMore = fetched.size() > size;
        List<TrialMessageItem> items = fetched.stream()
                .limit(size)
                .map(this::toItem)
                .toList();
        long latestMessageSequence = items.isEmpty()
                ? afterSequence
                : items.get(items.size() - 1).messageSequence();
        return new TrialMessagesResponse(items, latestMessageSequence, hasMore);
    }

    private TrialMessageItem toItem(ChatMessageEntity message) {
        UserEntity sender = message.getSender();
        return new TrialMessageItem(
                message.getId(),
                message.getSequenceNo(),
                message.getTrial().getId(),
                new TrialMessageSender(sender.getDemoKey(), sender.getNickname()),
                message.getContent(),
                message.getCreatedAt().toString()
        );
    }
}
