package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.config.AppAiProperties;
import com.skala.team6.webmini.common.model.TrialSide;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        String sideLabel = sideLabel(request.side());
        StatementPayload statement = request.statement();
        return new LawyerQuestionsResponse(
                List.of(
                        new GuideQuestionItem(
                                1,
                                "%s이 설명한 '%s' 상황에서 가장 중요했던 사실은 무엇인가요?"
                                        .formatted(sideLabel, statement.situation())
                        ),
                        new GuideQuestionItem(
                                2,
                                "%s이 '%s' 행동을 한 이유와 당시 의도는 무엇이었나요?"
                                        .formatted(sideLabel, statement.ownAction())
                        ),
                        new GuideQuestionItem(
                                3,
                                "%s이 원하는 해결인 '%s'을 위해 상대측과 합의할 기준은 무엇인가요?"
                                        .formatted(sideLabel, statement.desiredResolution())
                        )
                ),
                "1.0"
        );
    }

    @Override
    public LawyerArgumentResponse createArgumentDraft(
            AiRequestContext context,
            LawyerArgumentRequest request
    ) {
        String sideLabel = sideLabel(request.side());
        StatementPayload statement = request.statement();
        String answers = request.guideAnswers().stream()
                .map(GuideAnswerItem::answer)
                .collect(Collectors.joining(" "));
        return new LawyerArgumentResponse(
                "%s에 %s 상대측은 %s, %s은 %s 이후 %s"
                        .formatted(
                                statement.incidentTime(),
                                statement.situation(),
                                statement.counterpartAction(),
                                sideLabel,
                                statement.ownAction(),
                                statement.afterConversation()
                        ),
                "%s은 '%s'라는 해결을 요청합니다. 안내 답변에 따르면 %s"
                        .formatted(sideLabel, statement.desiredResolution(), answers),
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

    private String sideLabel(TrialSide side) {
        return side.name() + "측";
    }
}
