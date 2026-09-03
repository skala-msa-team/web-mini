package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrialQueryService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;

    public TrialQueryService(TrialRepository trialRepository,
                             TrialPartyRepository trialPartyRepository) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
    }

    @Transactional(readOnly = true)
    public TrialDetail findDetail(Long trialId) {
        TrialEntity trial = trialRepository.findById(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        List<TrialPartyEntity> parties = trialPartyRepository.findByTrialIdOrderBySideAsc(trialId);
        return new TrialDetail(trial, parties);
    }

    public record TrialDetail(TrialEntity trial, List<TrialPartyEntity> parties) {
    }
}
