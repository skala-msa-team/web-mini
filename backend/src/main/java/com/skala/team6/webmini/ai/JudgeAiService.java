package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.config.AppAiProperties;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JudgeAiService {
    private final AiClient aiClient;
    private final AppAiProperties properties;

    public JudgeAiService(AiClient aiClient, AppAiProperties properties) {
        this.aiClient = aiClient;
        this.properties = properties;
    }

    public Verdict createVerdict(Long trialId, String postSummary,
                                 Map<TrialSide, String> arguments) {
        JudgeVerdictResponse response = aiClient.createVerdict(
                new AiRequestContext("trial-verdict-" + trialId, properties.promptVersion()),
                new JudgeVerdictRequest(trialId, postSummary, arguments, properties.promptVersion()));
        if (response.winnerSide() == null
                || response.aFaultRatio() + response.bFaultRatio() != 100
                || response.summary() == null || response.summary().isBlank()
                || response.grounds() == null || response.grounds().isEmpty()
                || response.recommendations() == null) {
            throw new ApiException(ErrorCode.MOCK_AI_RESPONSE_INVALID);
        }
        return new Verdict(response.winnerSide(), response.aFaultRatio(), response.bFaultRatio(),
                response.summary(), response.grounds(), response.recommendations().a(),
                response.recommendations().b(), response.promptVersion());
    }

    public record Verdict(TrialSide winnerSide, int aFaultRatio, int bFaultRatio,
                          String summary, List<String> grounds, String aRecommendation,
                          String bRecommendation, String promptVersion) {
    }
}
