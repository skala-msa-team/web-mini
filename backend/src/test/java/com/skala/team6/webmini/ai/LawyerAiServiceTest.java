package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.exception.ApiException;
import com.skala.team6.webmini.common.exception.ErrorCode;
import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LawyerAiServiceTest {

    private final LawyerAiService.Statement statement = new LawyerAiService.Statement(
            "어제 저녁",
            "연락 문제로 다투었습니다.",
            "답장이 늦었습니다.",
            "반복해서 연락했습니다.",
            "감정이 상한 채 대화가 끝났습니다.",
            "연락 기준을 합의하고 싶습니다."
    );

    @Test
    void retriesInvalidQuestionsOnceWithSameRequestContext() {
        AiClient aiClient = mock(AiClient.class);
        when(aiClient.createGuideQuestions(any(), any()))
                .thenReturn(
                        new LawyerQuestionsResponse(List.of(), "1.0"),
                        new LawyerQuestionsResponse(
                                List.of(new GuideQuestionItem(1, "보완 질문")),
                                "1.0"
                        )
                );
        LawyerAiService service = new LawyerAiService(aiClient);

        List<LawyerAiService.GuideQuestion> result = service.createGuideQuestions(
                10L,
                TrialSide.A,
                RelationshipType.COUPLE,
                statement
        );

        assertThat(result).containsExactly(new LawyerAiService.GuideQuestion(1, "보완 질문"));
        ArgumentCaptor<AiRequestContext> contextCaptor =
                ArgumentCaptor.forClass(AiRequestContext.class);
        verify(aiClient, times(2)).createGuideQuestions(contextCaptor.capture(), any());
        assertThat(contextCaptor.getAllValues())
                .extracting(AiRequestContext::aiRequestId)
                .doesNotContainNull()
                .allMatch(contextCaptor.getValue().aiRequestId()::equals);
        assertThat(contextCaptor.getAllValues())
                .extracting(AiRequestContext::promptVersion)
                .containsOnly("lawyer-questions-v1");
    }

    @Test
    void convertsRepeatedAiFailureToCommonMockAiError() {
        AiClient aiClient = mock(AiClient.class);
        when(aiClient.createArgumentDraft(any(), any()))
                .thenThrow(new IllegalStateException("provider failure"));
        LawyerAiService service = new LawyerAiService(aiClient);

        assertThatThrownBy(() -> service.createArgumentDraft(
                10L,
                TrialSide.B,
                statement,
                List.of(new LawyerAiService.GuideAnswer(1, "질문", "답변"))
        )).isInstanceOfSatisfying(
                ApiException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.MOCK_AI_RESPONSE_INVALID)
        );
        verify(aiClient, times(2)).createArgumentDraft(any(), any());
    }
}
