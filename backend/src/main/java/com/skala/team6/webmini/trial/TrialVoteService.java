package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.VoteEntity;
import com.skala.team6.webmini.demo.DemoUserPersistenceService;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class TrialVoteService {
    private final TrialRepository trialRepository;
    private final VoteRepository voteRepository;
    private final DemoUserPersistenceService demoUserPersistenceService;

    public TrialVoteService(TrialRepository trialRepository,
                            VoteRepository voteRepository,
                            DemoUserPersistenceService demoUserPersistenceService) {
        this.trialRepository = trialRepository;
        this.voteRepository = voteRepository;
        this.demoUserPersistenceService = demoUserPersistenceService;
    }

    @Transactional
    public VoteResult vote(Long trialId, String demoUserId, TrialSide selectedSide) {
        var trial = trialRepository.findByIdForUpdate(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        OffsetDateTime now = OffsetDateTime.now();
        if (trial.getStatus() != TrialStatus.VOTING
                || trial.getPhaseEndsAt() == null
                || !trial.getPhaseEndsAt().isAfter(now)) {
            throw new ApiException(ErrorCode.VOTING_NOT_OPEN);
        }

        var voter = demoUserPersistenceService.getOrCreate(demoUserId);
        if (voteRepository.existsByTrialIdAndVoterId(trialId, voter.getId())) {
            throw new ApiException(ErrorCode.ALREADY_VOTED);
        }

        VoteEntity saved = voteRepository.saveAndFlush(
                new VoteEntity(trial, voter, selectedSide));
        return new VoteResult(saved.getSelectedSide(), saved.getCreatedAt());
    }

    public record VoteResult(TrialSide selectedSide, OffsetDateTime votedAt) {
    }
}
