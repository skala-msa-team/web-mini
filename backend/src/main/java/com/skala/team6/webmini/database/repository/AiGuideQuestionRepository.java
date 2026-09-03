package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.AiGuideQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiGuideQuestionRepository extends JpaRepository<AiGuideQuestionEntity, Long> {
    List<AiGuideQuestionEntity> findByTrialPartyIdOrderBySequenceNoAsc(Long trialPartyId);
}
