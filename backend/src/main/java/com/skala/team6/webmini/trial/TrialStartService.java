package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.config.TrialTimingProperties;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSpeaker;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialEventEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.repository.TrialEventRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class TrialStartService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;
    private final TrialEventRepository trialEventRepository;
    private final TrialTimingProperties timings;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public TrialStartService(
            TrialRepository trialRepository,
            TrialPartyRepository trialPartyRepository,
            TrialEventRepository trialEventRepository,
            TrialTimingProperties timings,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
        this.trialEventRepository = trialEventRepository;
        this.timings = timings;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public StartedTrial start(Long trialId) {
        TrialEntity trial = trialRepository.findByIdForUpdate(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        if (trial.getStatus() != TrialStatus.PREPARING) {
            throw new ApiException(ErrorCode.TRIAL_ALREADY_STARTED);
        }

        var parties = trialPartyRepository.findByTrialIdOrderBySideAsc(trialId);
        boolean bothReady = parties.size() == 2
                && parties.stream().allMatch(TrialPartyEntity::isReady);
        if (!bothReady) {
            throw new ApiException(ErrorCode.PARTIES_NOT_READY);
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime phaseEndsAt = now.plusSeconds(timings.introductionSeconds());
        OffsetDateTime scheduledEndAt = phaseEndsAt
                .plusSeconds(timings.argumentSeconds() * 2 + timings.votingSeconds());
        trial.startPhase(TrialStatus.INTRODUCTION, now, phaseEndsAt);
        trial.scheduleEnd(scheduledEndAt);

        saveEvent(trial, 1, "TRIAL_STARTED", TrialSpeaker.SYSTEM, null, Map.of(
                "status", TrialStatus.INTRODUCTION.name(),
                "startedAt", now.toString(),
                "phaseEndsAt", phaseEndsAt.toString(),
                "scheduledEndAt", scheduledEndAt.toString()));
        saveEvent(trial, 2, "JUDGE_INTRODUCTION", TrialSpeaker.JUDGE,
                "지금부터 재판을 시작합니다. " + trial.getPost().getTitle(), Map.of(
                        "status", TrialStatus.INTRODUCTION.name(),
                        "phaseEndsAt", phaseEndsAt.toString()));
        return new StartedTrial(trial, 2);
    }

    private void saveEvent(TrialEntity trial, long sequence, String type,
                           TrialSpeaker speaker, String content, Map<String, Object> payload) {
        TrialEventEntity saved = trialEventRepository.saveAndFlush(new TrialEventEntity(
                trial, sequence, type, speaker, content, objectMapper.writeValueAsString(payload)));
        eventPublisher.publishEvent(new TrialEventSavedEvent(new TrialEventMessage(
                saved.getId(), trial.getId(), sequence, type, speaker, content,
                saved.getCreatedAt(), payload)));
    }

    public record StartedTrial(TrialEntity trial, long latestEventSequence) {
    }
}
