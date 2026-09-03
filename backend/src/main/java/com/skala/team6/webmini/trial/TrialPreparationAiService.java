package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.ai.LawyerAiService;
import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.database.entity.AiGuideQuestionEntity;
import com.skala.team6.webmini.database.entity.TrialEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.entity.TrialStatementEntity;
import com.skala.team6.webmini.database.repository.AiGuideQuestionRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import com.skala.team6.webmini.database.repository.TrialStatementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrialPreparationAiService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;
    private final TrialStatementRepository trialStatementRepository;
    private final AiGuideQuestionRepository questionRepository;
    private final LawyerAiService lawyerAiService;

    public TrialPreparationAiService(
            TrialRepository trialRepository,
            TrialPartyRepository trialPartyRepository,
            TrialStatementRepository trialStatementRepository,
            AiGuideQuestionRepository questionRepository,
            LawyerAiService lawyerAiService
    ) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
        this.trialStatementRepository = trialStatementRepository;
        this.questionRepository = questionRepository;
        this.lawyerAiService = lawyerAiService;
    }

    @Transactional
    public List<AiGuideQuestionEntity> createGuideQuestions(Long trialId, TrialSide side) {
        TrialEntity trial = findPreparingTrial(trialId);
        TrialPartyEntity party = findParty(trialId, side);
        TrialStatementEntity statement = findStatement(party);
        List<AiGuideQuestionEntity> existing =
                questionRepository.findByTrialPartyIdOrderBySequenceNoAsc(party.getId());
        if (!existing.isEmpty()) {
            return existing;
        }

        List<AiGuideQuestionEntity> questions = lawyerAiService.createGuideQuestions(
                        trialId,
                        side,
                        trial.getPost().getRelationshipType(),
                        toStatement(statement)
                ).stream()
                .map(question -> new AiGuideQuestionEntity(
                        party,
                        question.sequence(),
                        question.question()
                ))
                .toList();
        return questionRepository.saveAll(questions);
    }

    @Transactional
    public TrialStatementEntity createArgumentDraft(Long trialId, TrialSide side) {
        findPreparingTrial(trialId);
        TrialPartyEntity party = findParty(trialId, side);
        TrialStatementEntity statement = findStatement(party);
        if (hasText(statement.getFactSummary()) && hasText(statement.getArgumentText())) {
            return statement;
        }

        List<AiGuideQuestionEntity> questions =
                questionRepository.findByTrialPartyIdOrderBySequenceNoAsc(party.getId());
        if (questions.isEmpty() || questions.stream().anyMatch(question -> !hasText(question.getAnswer()))) {
            throw new ApiException(ErrorCode.GUIDE_ANSWERS_INCOMPLETE);
        }

        LawyerAiService.ArgumentDraft draft = lawyerAiService.createArgumentDraft(
                trialId,
                side,
                toStatement(statement),
                questions.stream()
                        .map(question -> new LawyerAiService.GuideAnswer(
                                question.getSequenceNo(),
                                question.getQuestion(),
                                question.getAnswer()
                        ))
                        .toList()
        );
        statement.updateArgumentDraft(draft.factSummary(), draft.argumentText());
        return trialStatementRepository.save(statement);
    }

    private TrialEntity findPreparingTrial(Long trialId) {
        TrialEntity trial = trialRepository.findByIdForUpdate(trialId)
                .orElseThrow(() -> new ApiException(ErrorCode.TRIAL_NOT_FOUND));
        if (trial.getStatus() != TrialStatus.PREPARING) {
            throw new ApiException(ErrorCode.TRIAL_NOT_PREPARING);
        }
        return trial;
    }

    private TrialPartyEntity findParty(Long trialId, TrialSide side) {
        return trialPartyRepository.findByTrialIdAndSide(trialId, side)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_TRIAL_SIDE));
    }

    private TrialStatementEntity findStatement(TrialPartyEntity party) {
        return trialStatementRepository.findByTrialPartyId(party.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.ARGUMENT_DRAFT_REQUIRED));
    }

    private LawyerAiService.Statement toStatement(TrialStatementEntity statement) {
        return new LawyerAiService.Statement(
                statement.getIncidentTime(),
                statement.getSituation(),
                statement.getCounterpartAction(),
                statement.getOwnAction(),
                statement.getAfterConversation(),
                statement.getDesiredResolution()
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
