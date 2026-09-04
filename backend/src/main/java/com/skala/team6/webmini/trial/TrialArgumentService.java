package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.TrialStatementEntity;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class TrialArgumentService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;
    private final TrialStatementRepository trialStatementRepository;

    public TrialArgumentService(
            TrialRepository trialRepository,
            TrialPartyRepository trialPartyRepository,
            TrialStatementRepository trialStatementRepository
    ) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
        this.trialStatementRepository = trialStatementRepository;
    }

    @Transactional
    public TrialStatementEntity updateDraft(
            Long trialId,
            TrialSide side,
            UpdateArgumentDraftRequest request
    ) {
        TrialPartyEntity party = findParty(trialId, side);
        TrialStatementEntity statement = trialStatementRepository.findByTrialPartyId(party.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.ARGUMENT_DRAFT_REQUIRED));
        statement.updateArgumentDraft(
                request.factSummary().trim(),
                request.argumentText().trim()
        );
        return trialStatementRepository.save(statement);
    }

    @Transactional
    public ConfirmedArgument confirm(Long trialId, TrialSide side) {
        TrialPartyEntity party = findParty(trialId, side);
        TrialStatementEntity statement = trialStatementRepository.findByTrialPartyId(party.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.ARGUMENT_DRAFT_REQUIRED));
        if (!hasText(statement.getFactSummary()) || !hasText(statement.getArgumentText())) {
            throw new ApiException(ErrorCode.ARGUMENT_DRAFT_REQUIRED);
        }

        statement.confirm(OffsetDateTime.now());
        party.markReady();
        trialStatementRepository.save(statement);
        trialPartyRepository.save(party);

        var parties = trialPartyRepository.findByTrialIdOrderBySideAsc(trialId);
        boolean bothConfirmed = parties.size() == 2
                && parties.stream().allMatch(TrialPartyEntity::isReady);
        return new ConfirmedArgument(statement, bothConfirmed);
    }

    private TrialPartyEntity findParty(Long trialId, TrialSide side) {
        if (!trialRepository.existsById(trialId)) {
            throw new ApiException(ErrorCode.TRIAL_NOT_FOUND);
        }
        return trialPartyRepository.findByTrialIdAndSide(trialId, side)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_TRIAL_SIDE));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public record ConfirmedArgument(
            TrialStatementEntity statement,
            boolean bothConfirmed
    ) {
    }
}
