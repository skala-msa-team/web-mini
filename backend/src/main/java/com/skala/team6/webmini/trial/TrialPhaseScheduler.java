package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.repository.TrialRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.trial", name = "scheduler-enabled",
        havingValue = "true", matchIfMissing = true)
public class TrialPhaseScheduler {
    private static final List<TrialStatus> ACTIVE_PHASES = List.of(
            TrialStatus.INTRODUCTION, TrialStatus.A_ARGUMENT, TrialStatus.B_ARGUMENT,
            TrialStatus.DEBATE, TrialStatus.VOTING, TrialStatus.VERDICT);

    private final TrialRepository trialRepository;
    private final TrialPhaseService phaseService;

    public TrialPhaseScheduler(TrialRepository trialRepository, TrialPhaseService phaseService) {
        this.trialRepository = trialRepository;
        this.phaseService = phaseService;
    }

    @Scheduled(fixedDelayString = "${app.trial.scheduler-interval-millis:1000}")
    public void advanceExpiredTrials() {
        OffsetDateTime now = OffsetDateTime.now();
        for (Long trialId : trialRepository.findTrialIdsByStatus(TrialStatus.DEBATE)) {
            phaseService.publishDueDebateTurns(trialId, now);
        }
        for (Long trialId : trialRepository.findExpiredTrialIds(ACTIVE_PHASES, now)) {
            phaseService.advanceIfExpired(trialId, now);
        }
    }
}
