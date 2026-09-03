package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    boolean existsByTrialIdAndVoterId(Long trialId, Long voterId);

    long countByTrialIdAndSelectedSide(Long trialId, TrialSide selectedSide);
}
