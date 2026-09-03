package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrialPartyRepository extends JpaRepository<TrialPartyEntity, Long> {
    List<TrialPartyEntity> findByTrialIdOrderBySideAsc(Long trialId);

    Optional<TrialPartyEntity> findByTrialIdAndSide(Long trialId, TrialSide side);
}
