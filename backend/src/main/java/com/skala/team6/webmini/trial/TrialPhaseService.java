package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.ai.JudgeAiService;
import com.skala.team6.webmini.common.config.TrialTimingProperties;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialSpeaker;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.TrialStatementEntity;
import com.skala.team6.webmini.database.entity.VerdictEntity;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import com.skala.team6.webmini.database.repository.VerdictRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.Map;

@Service
public class TrialPhaseService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository partyRepository;
    private final TrialStatementRepository statementRepository;
    private final VerdictRepository verdictRepository;
    private final TrialTimingProperties timings;
    private final TrialEventWriter eventWriter;
    private final JudgeAiService judgeAiService;
    private final ObjectMapper objectMapper;

    public TrialPhaseService(TrialRepository trialRepository,
                             TrialPartyRepository partyRepository,
                             TrialStatementRepository statementRepository,
                             VerdictRepository verdictRepository,
                             TrialTimingProperties timings,
                             TrialEventWriter eventWriter,
                             JudgeAiService judgeAiService,
                             ObjectMapper objectMapper) {
        this.trialRepository = trialRepository;
        this.partyRepository = partyRepository;
        this.statementRepository = statementRepository;
        this.verdictRepository = verdictRepository;
        this.timings = timings;
        this.eventWriter = eventWriter;
        this.judgeAiService = judgeAiService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public boolean advanceIfExpired(Long trialId, OffsetDateTime now) {
        TrialEntity trial = trialRepository.findByIdForUpdate(trialId).orElse(null);
        if (trial == null || trial.getPhaseEndsAt() == null
                || trial.getPhaseEndsAt().isAfter(now)) {
            return false;
        }
        switch (trial.getStatus()) {
            case INTRODUCTION -> openArgument(trial, TrialSide.A, now);
            case A_ARGUMENT -> openArgument(trial, TrialSide.B, now);
            case B_ARGUMENT -> openVoting(trial, now);
            case VOTING -> publishVerdict(trial, now);
            case VERDICT -> endTrial(trial, now);
            default -> { return false; }
        }
        return true;
    }

    private void openArgument(TrialEntity trial, TrialSide side, OffsetDateTime now) {
        TrialStatus status = side == TrialSide.A ? TrialStatus.A_ARGUMENT : TrialStatus.B_ARGUMENT;
        OffsetDateTime endsAt = now.plusSeconds(timings.argumentSeconds());
        trial.startPhase(status, now, endsAt);
        TrialPartyEntity party = partyRepository.findByTrialIdAndSide(trial.getId(), side).orElseThrow();
        TrialStatementEntity statement = statementRepository.findByTrialPartyId(party.getId()).orElseThrow();
        TrialSpeaker speaker = side == TrialSide.A ? TrialSpeaker.A_LAWYER : TrialSpeaker.B_LAWYER;
        eventWriter.save(trial, status.name(), speaker, statement.getArgumentText(), Map.of(
                "side", side.name(), "status", status.name(), "phaseEndsAt", endsAt.toString()));
    }

    private void openVoting(TrialEntity trial, OffsetDateTime now) {
        OffsetDateTime endsAt = now.plusSeconds(timings.votingSeconds());
        trial.startPhase(TrialStatus.VOTING, now, endsAt);
        eventWriter.save(trial, "VOTING_STARTED", TrialSpeaker.SYSTEM, null, Map.of(
                "status", TrialStatus.VOTING.name(), "voteStartedAt", now.toString(),
                "voteEndsAt", endsAt.toString(), "allowedSides", new String[]{"A", "B"}));
    }

    private void publishVerdict(TrialEntity trial, OffsetDateTime now) {
        Map<TrialSide, String> arguments = new EnumMap<>(TrialSide.class);
        for (TrialPartyEntity party : partyRepository.findByTrialIdOrderBySideAsc(trial.getId())) {
            arguments.put(party.getSide(), statementRepository.findByTrialPartyId(party.getId())
                    .orElseThrow().getArgumentText());
        }
        JudgeAiService.Verdict result = judgeAiService.createVerdict(
                trial.getId(), trial.getPost().getContent(), arguments);
        VerdictEntity verdict = verdictRepository.saveAndFlush(new VerdictEntity(
                trial, result.winnerSide(), result.aFaultRatio(), result.bFaultRatio(),
                result.summary(), objectMapper.writeValueAsString(result.grounds()),
                result.aRecommendation(), result.bRecommendation(), result.promptVersion()));
        trial.startPhase(TrialStatus.VERDICT, now, now);
        eventWriter.save(trial, "VERDICT_PUBLISHED", TrialSpeaker.JUDGE, result.summary(), Map.of(
                "status", TrialStatus.VERDICT.name(), "verdictId", verdict.getId(),
                "winnerSide", result.winnerSide().name(), "aFaultRatio", result.aFaultRatio(),
                "bFaultRatio", result.bFaultRatio(), "publishedAt", now.toString()));
    }

    private void endTrial(TrialEntity trial, OffsetDateTime now) {
        trial.complete(now);
        eventWriter.save(trial, "TRIAL_ENDED", TrialSpeaker.SYSTEM, null, Map.of(
                "status", TrialStatus.ENDED.name(), "endedAt", now.toString(),
                "resultPath", "/trials/" + trial.getId() + "/result"));
    }
}
