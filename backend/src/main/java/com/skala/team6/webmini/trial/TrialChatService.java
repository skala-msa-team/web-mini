package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;
import com.skala.team6.webmini.database.entity.ChatMessageEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.UserEntity;
import com.skala.team6.webmini.database.repository.ChatMessageRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.demo.DemoUserPersistenceService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
public class TrialChatService {
    private static final int MAX_CONTENT_LENGTH = 500;
    private static final Set<TrialStatus> CHAT_ALLOWED_STATUSES = EnumSet.of(
            TrialStatus.INTRODUCTION,
            TrialStatus.A_ARGUMENT,
            TrialStatus.B_ARGUMENT,
            TrialStatus.DEBATE,
            TrialStatus.VOTING,
            TrialStatus.VERDICT
    );

    private final TrialRepository trialRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DemoUserPersistenceService demoUserPersistenceService;
    private final ApplicationEventPublisher eventPublisher;

    public TrialChatService(
            TrialRepository trialRepository,
            ChatMessageRepository chatMessageRepository,
            DemoUserPersistenceService demoUserPersistenceService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.trialRepository = trialRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.demoUserPersistenceService = demoUserPersistenceService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TrialChatMessagePayload send(Long trialId, String demoUserId, String rawContent) {
        TrialEntity trial = trialRepository.findByIdForUpdate(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        validateChatAllowed(trial);

        String content = validateContent(rawContent);

        UserEntity sender = demoUserPersistenceService.getOrCreate(demoUserId);
        long sequence = chatMessageRepository.findLatestSequenceByTrialId(trialId) + 1;
        ChatMessageEntity message = chatMessageRepository.saveAndFlush(
                new ChatMessageEntity(trial, sender, sequence, content));
        TrialChatMessagePayload payload = toPayload(message);
        eventPublisher.publishEvent(new TrialChatSavedEvent(payload));
        return payload;
    }

    private void validateChatAllowed(TrialEntity trial) {
        if (trial.getVisibility() != Visibility.PUBLIC
                || !CHAT_ALLOWED_STATUSES.contains(trial.getStatus())) {
            throw new ApiException(ErrorCode.CHAT_NOT_ALLOWED);
        }
    }

    private String validateContent(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR);
        }
        String content = rawContent.trim();
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new ApiException(ErrorCode.MESSAGE_TOO_LONG);
        }
        return content;
    }

    private TrialChatMessagePayload toPayload(ChatMessageEntity message) {
        UserEntity sender = message.getSender();
        return new TrialChatMessagePayload(
                message.getId(),
                message.getSequenceNo(),
                message.getTrial().getId(),
                new TrialMessageSender(sender.getDemoKey(), sender.getNickname()),
                message.getContent(),
                message.getCreatedAt().toString()
        );
    }
}
