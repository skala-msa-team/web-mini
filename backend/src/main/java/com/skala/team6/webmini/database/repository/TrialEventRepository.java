package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.TrialEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrialEventRepository extends JpaRepository<TrialEventEntity, Long> {
    List<TrialEventEntity> findByTrialIdOrderBySequenceNoAsc(Long trialId);

    List<TrialEventEntity> findByTrialIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
            Long trialId,
            long sequenceNo
    );

    @Query("select coalesce(max(event.sequenceNo), 0) "
            + "from TrialEventEntity event where event.trial.id = :trialId")
    long findLatestSequenceByTrialId(@Param("trialId") Long trialId);

    long countByTrialIdAndEventTypeIn(Long trialId, List<String> eventTypes);
}
