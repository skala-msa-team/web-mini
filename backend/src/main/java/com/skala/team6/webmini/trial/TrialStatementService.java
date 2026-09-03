package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.TrialStatementEntity;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrialStatementService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;
    private final TrialStatementRepository trialStatementRepository;

    public TrialStatementService(
            TrialRepository trialRepository,
            TrialPartyRepository trialPartyRepository,
            TrialStatementRepository trialStatementRepository
    ) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
        this.trialStatementRepository = trialStatementRepository;
    }

    @Transactional
    public TrialStatementEntity save(Long trialId, TrialSide side, StatementRequest request) {
        TrialPartyEntity party = findParty(trialId, side);
        TrialStatementEntity statement = trialStatementRepository.findByTrialPartyId(party.getId())
                .orElseGet(() -> new TrialStatementEntity(
                        party,
                        request.incidentTime().trim(),
                        request.situation().trim(),
                        request.counterpartAction().trim(),
                        request.ownAction().trim(),
                        request.afterConversation().trim(),
                        request.desiredResolution().trim()
                ));
        statement.updateStatement(
                request.incidentTime().trim(),
                request.situation().trim(),
                request.counterpartAction().trim(),
                request.ownAction().trim(),
                request.afterConversation().trim(),
                request.desiredResolution().trim()
        );
        return trialStatementRepository.save(statement);
    }

    private TrialPartyEntity findParty(Long trialId, TrialSide side) {
        if (!trialRepository.existsById(trialId)) {
            throw new ApiException(ErrorCode.TRIAL_NOT_FOUND);
        }
        return trialPartyRepository.findByTrialIdAndSide(trialId, side)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_TRIAL_SIDE));
    }
}
