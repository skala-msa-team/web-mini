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
import java.time.Duration;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;

@Service
public class TrialPhaseService {
    private static final List<DebateTurn> DEBATE_TURNS = List.of(
            new DebateTurn(TrialSpeaker.A_LAWYER,
                    "B측은 이성 친구와의 만남을 미리 공유하기로 한 약속을 지키지 않았습니다. "
                            + "문제는 만남 자체보다 상대방의 불안을 고려하지 않은 소통 방식입니다."),
            new DebateTurn(TrialSpeaker.B_LAWYER,
                    "사전 공유를 놓친 점은 인정합니다. 다만 갑작스럽게 정해진 자리였고, "
                            + "A측을 속이거나 관계를 훼손하려는 의도는 없었습니다."),
            new DebateTurn(TrialSpeaker.A_LAWYER,
                    "의도가 없었다고 해도 반복된 설명 부족은 신뢰를 약화시킵니다. "
                            + "갈등 이후에도 A측이 충분히 납득할 수 있는 설명이 필요했습니다."),
            new DebateTurn(TrialSpeaker.B_LAWYER,
                    "B측은 늦게라도 사실을 설명하고 사과했습니다. 한 번의 판단 착오를 "
                            + "지속적인 기만으로 단정하면 관계 회복을 위한 대화의 여지가 줄어듭니다."),
            new DebateTurn(TrialSpeaker.A_LAWYER,
                    "A측의 감정적 대응에 아쉬움이 있더라도 최초 갈등의 원인은 약속 위반입니다. "
                            + "재발 방지를 위한 분명한 책임 인정이 선행되어야 합니다."),
            new DebateTurn(TrialSpeaker.B_LAWYER,
                    "B측은 책임을 피하지 않습니다. 다만 양측이 연락 기준을 다시 합의하고 "
                            + "감정을 확인하는 방식으로 관계를 회복할 기회를 주어야 합니다.")
    );
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
            case B_ARGUMENT -> openDebate(trial, now);
            case DEBATE -> {
                saveDueDebateTurns(trial, now);
                openVoting(trial, now);
            }
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

    private void openDebate(TrialEntity trial, OffsetDateTime now) {
        OffsetDateTime endsAt = now.plusSeconds(timings.debateSeconds());
        trial.startPhase(TrialStatus.DEBATE, now, endsAt);
        eventWriter.save(trial, "DEBATE_STARTED", TrialSpeaker.SYSTEM, null, Map.of(
                "status", TrialStatus.DEBATE.name(),
                "phaseEndsAt", endsAt.toString(),
                "totalTurns", DEBATE_TURNS.size()));
        saveDueDebateTurns(trial, now);
    }

    @Transactional
    public void publishDueDebateTurns(Long trialId, OffsetDateTime now) {
        TrialEntity trial = trialRepository.findByIdForUpdate(trialId).orElse(null);
        if (trial == null || trial.getStatus() != TrialStatus.DEBATE) {
            return;
        }
        saveDueDebateTurns(trial, now);
    }

    private void saveDueDebateTurns(TrialEntity trial, OffsetDateTime now) {
        OffsetDateTime startedAt = trial.getPhaseStartedAt();
        if (startedAt == null || trial.getPhaseEndsAt() == null) {
            return;
        }

        long elapsedSeconds = Math.max(0, Duration.between(startedAt, now).getSeconds());
        long dueTurnCount = Math.min(
                DEBATE_TURNS.size(),
                elapsedSeconds * DEBATE_TURNS.size() / timings.debateSeconds() + 1
        );
        long savedTurnCount = eventWriter.countByTrialAndTypes(
                trial.getId(), "A_DEBATE", "B_DEBATE");

        for (long turnIndex = savedTurnCount; turnIndex < dueTurnCount; turnIndex++) {
            DebateTurn turn = DEBATE_TURNS.get((int) turnIndex);
            String type = turn.speaker() == TrialSpeaker.A_LAWYER ? "A_DEBATE" : "B_DEBATE";
            eventWriter.save(trial, type, turn.speaker(), turn.content(), Map.of(
                    "status", TrialStatus.DEBATE.name(),
                    "phaseEndsAt", trial.getPhaseEndsAt().toString(),
                    "turn", turnIndex + 1,
                    "totalTurns", DEBATE_TURNS.size()));
        }
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
        eventWriter.save(trial, "VERDICT_ANNOUNCED", TrialSpeaker.JUDGE, result.summary(), Map.of(
                "status", TrialStatus.VERDICT.name(), "verdictId", verdict.getId(),
                "winnerSide", result.winnerSide().name(), "aFaultRatio", result.aFaultRatio(),
                "bFaultRatio", result.bFaultRatio(), "publishedAt", now.toString()));
    }

    private void endTrial(TrialEntity trial, OffsetDateTime now) {
        trial.complete(now);
        eventWriter.save(trial, "TRIAL_ENDED", TrialSpeaker.SYSTEM, null, Map.of(
                "status", TrialStatus.ENDED.name(), "endedAt", now.toString(),
                "resultPath", "/api/v1/trials/" + trial.getId() + "/results"));
    }

    private record DebateTurn(TrialSpeaker speaker, String content) {
    }
}
