package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.VerdictRepository;
import com.skala.team6.webmini.database.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class TrialResultService {
    private final TrialRepository trialRepository;
    private final VerdictRepository verdictRepository;
    private final VoteRepository voteRepository;
    private final ObjectMapper objectMapper;

    public TrialResultService(TrialRepository trialRepository,
            VerdictRepository verdictRepository,
            VoteRepository voteRepository,
            ObjectMapper objectMapper) {
        this.trialRepository = trialRepository;
        this.verdictRepository = verdictRepository;
        this.voteRepository = voteRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public TrialResultResponse getResult(Long trialId) {
        var trial = trialRepository.findById(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        if (trial.getStatus() != TrialStatus.ENDED && trial.getStatus() != TrialStatus.VERDICT) {
            throw new ApiException(ErrorCode.RESULT_NOT_FOUND);
        }
        var verdict = verdictRepository.findByTrialId(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESULT_NOT_FOUND));
        List<String> grounds = readGrounds(verdict.getGrounds());
        int aVotes = toInt(voteRepository.countByTrialIdAndSelectedSide(trialId, TrialSide.A));
        int bVotes = toInt(voteRepository.countByTrialIdAndSelectedSide(trialId, TrialSide.B));
        return new TrialResultResponse(
                trialId,
                new VerdictPayload(verdict.getWinnerSide(), verdict.getAFaultRatio(),
                        verdict.getBFaultRatio(), verdict.getSummary(), grounds,
                        verdict.getARecommendation(), verdict.getBRecommendation()),
                new PublicVotePayload(aVotes, bVotes, aVotes + bVotes));
    }

    private List<String> readGrounds(String grounds) {
        try {
            return List.of(objectMapper.readValue(grounds, String[].class));
        } catch (Exception exception) {
            throw new ApiException(ErrorCode.RESULT_NOT_FOUND);
        }
    }

    private int toInt(long count) {
        return Math.toIntExact(count);
    }
}
