package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.config.AppAiProperties;
import com.skala.team6.webmini.common.model.TrialSide;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockAiClient implements AiClient {

    private final AppAiProperties appAiProperties;

    public MockAiClient(AppAiProperties appAiProperties) {
        this.appAiProperties = appAiProperties;
    }

    @Override
    public LawyerQuestionsResponse createGuideQuestions(
            AiRequestContext context,
            LawyerQuestionsRequest request
    ) {
        return new LawyerQuestionsResponse(
                List.of(new GuideQuestionItem(1, "평소 두 분이 합의한 연락 기준이 있었나요?")),
                "1.0"
        );
    }

    @Override
    public LawyerArgumentResponse createArgumentDraft(
            AiRequestContext context,
            LawyerArgumentRequest request
    ) {
        return new LawyerArgumentResponse(
                "양측은 연락 빈도에 대한 명확한 합의가 없었습니다.",
                "A측은 불안감 때문에 반복 연락했으나 사전 합의가 없었다고 주장합니다.",
                "1.0"
        );
    }

    @Override
    public JudgeVerdictResponse createVerdict(
            AiRequestContext context,
            JudgeVerdictRequest request
    ) {
        return new JudgeVerdictResponse(
                TrialSide.B,
                60,
                40,
                "판결 요지",
                List.of("연락 기준에 대한 사전 합의가 없었습니다."),
                new RecommendationPair(
                        "불안할 때 반복 연락 전 의사를 확인합니다.",
                        "답장이 어려운 시간을 미리 공유합니다."
                ),
                "1.0",
                request.promptVersion().isBlank()
                        ? appAiProperties.promptVersion()
                        : request.promptVersion()
        );
    }
}
