package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.VerdictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerdictRepository extends JpaRepository<VerdictEntity, Long> {
    Optional<VerdictEntity> findByTrialId(Long trialId);
}
