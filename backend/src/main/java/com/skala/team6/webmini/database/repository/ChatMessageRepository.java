package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByTrialIdOrderBySequenceNoAsc(Long trialId);

    List<ChatMessageEntity> findByTrialIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
            Long trialId,
            long sequenceNo
    );

    @EntityGraph(attributePaths = "sender")
    List<ChatMessageEntity> findByTrialIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
            Long trialId,
            long sequenceNo,
            Pageable pageable
    );

    @Query("select coalesce(max(message.sequenceNo), 0) "
            + "from ChatMessageEntity message where message.trial.id = :trialId")
    long findLatestSequenceByTrialId(@Param("trialId") Long trialId);
}
