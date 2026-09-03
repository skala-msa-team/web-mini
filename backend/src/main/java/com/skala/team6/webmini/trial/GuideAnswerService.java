package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.database.entity.AiGuideQuestionEntity;
import com.skala.team6.webmini.database.entity.TrialPartyEntity;
import com.skala.team6.webmini.database.repository.AiGuideQuestionRepository;
import com.skala.team6.webmini.database.repository.TrialPartyRepository;
import com.skala.team6.webmini.database.repository.TrialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class GuideAnswerService {
    private final TrialRepository trialRepository;
    private final TrialPartyRepository trialPartyRepository;
    private final AiGuideQuestionRepository questionRepository;

    public GuideAnswerService(
            TrialRepository trialRepository,
            TrialPartyRepository trialPartyRepository,
            AiGuideQuestionRepository questionRepository
    ) {
        this.trialRepository = trialRepository;
        this.trialPartyRepository = trialPartyRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public SavedGuideAnswers save(Long trialId, TrialSide side, GuideAnswersRequest request) {
        TrialPartyEntity party = findParty(trialId, side);
        List<AiGuideQuestionEntity> questions =
                questionRepository.findByTrialPartyIdOrderBySequenceNoAsc(party.getId());

        for (GuideAnswerPayload payload : request.answers()) {
            AiGuideQuestionEntity question = questions.stream()
                    .filter(candidate -> candidate.getId().equals(payload.questionId()))
                    .findFirst()
                    .orElseThrow(() -> new ApiException(ErrorCode.GUIDE_QUESTION_NOT_FOUND));
            question.answer(payload.answer().trim(), OffsetDateTime.now());
        }
        questionRepository.saveAll(questions);

        boolean allAnswered = !questions.isEmpty()
                && questions.stream().allMatch(question -> hasText(question.getAnswer()));
        List<GuideAnswerPayload> answers = request.answers().stream()
                .map(answer -> new GuideAnswerPayload(answer.questionId(), answer.answer().trim()))
                .toList();
        return new SavedGuideAnswers(answers, allAnswered);
    }

    public boolean areAllAnswered(Long trialPartyId) {
        List<AiGuideQuestionEntity> questions =
                questionRepository.findByTrialPartyIdOrderBySequenceNoAsc(trialPartyId);
        return !questions.isEmpty()
                && questions.stream().allMatch(question -> hasText(question.getAnswer()));
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

    public record SavedGuideAnswers(
            List<GuideAnswerPayload> answers,
            boolean allAnswered
    ) {
    }
}
