package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrialStartService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;

    public TrialStartService(
            TrialRepository trialRepository,
            TrialPartyRepository trialPartyRepository
    ) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
    }

    @Transactional(readOnly = true)
    public void validateReady(Long trialId) {
        var trial = trialRepository.findById(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        if (trial.getStatus() != TrialStatus.PREPARING) {
            throw new ApiException(ErrorCode.TRIAL_NOT_PREPARING);
        }

        var parties = trialPartyRepository.findByTrialIdOrderBySideAsc(trialId);
        boolean bothReady = parties.size() == 2
                && parties.stream().allMatch(TrialPartyEntity::isReady);
        if (!bothReady) {
            throw new ApiException(ErrorCode.PARTIES_NOT_READY);
        }
    }
}
