package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByTrialIdOrderBySequenceNoAsc(Long trialId);

    List<ChatMessageEntity> findByTrialIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
            Long trialId,
            long sequenceNo
    );
}
