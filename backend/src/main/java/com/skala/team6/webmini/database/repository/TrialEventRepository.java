package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.TrialEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrialEventRepository extends JpaRepository<TrialEventEntity, Long> {
    List<TrialEventEntity> findByTrialIdOrderBySequenceNoAsc(Long trialId);

    List<TrialEventEntity> findByTrialIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
            Long trialId,
            long sequenceNo
    );
}
