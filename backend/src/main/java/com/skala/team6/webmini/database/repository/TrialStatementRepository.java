package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.TrialStatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrialStatementRepository extends JpaRepository<TrialStatementEntity, Long> {
    Optional<TrialStatementEntity> findByTrialPartyId(Long trialPartyId);
}
