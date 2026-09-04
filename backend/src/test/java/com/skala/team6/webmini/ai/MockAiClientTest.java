package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.config.AppAiProperties;
import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MockAiClientTest {

    private final MockAiClient mockAiClient =
            new MockAiClient(new AppAiProperties("mock", "judge-v1"));
    private final StatementPayload statement = new StatementPayload(
            "어제 저녁",
            "연락 문제로 다투었습니다.",
            "답장이 늦었습니다.",
            "반복해서 연락했습니다.",
            "감정이 상한 채 대화가 끝났습니다.",
            "연락 기준을 합의하고 싶습니다."
    );

    @Test
    void createsDeterministicGuideQuestionsForEachSide() {
        AiRequestContext context = new AiRequestContext(
                "mock-questions-request", "lawyer-questions-v1");
        LawyerQuestionsRequest sideARequest = new LawyerQuestionsRequest(
                10L, TrialSide.A, RelationshipType.COUPLE, statement);
        LawyerQuestionsRequest sideBRequest = new LawyerQuestionsRequest(
                10L, TrialSide.B, RelationshipType.COUPLE, statement);

        LawyerQuestionsResponse firstSideA =
                mockAiClient.createGuideQuestions(context, sideARequest);
        LawyerQuestionsResponse secondSideA =
                mockAiClient.createGuideQuestions(context, sideARequest);
        LawyerQuestionsResponse sideB =
                mockAiClient.createGuideQuestions(context, sideBRequest);

        assertThat(firstSideA).isEqualTo(secondSideA);
        assertThat(firstSideA.schemaVersion()).isEqualTo("1.0");
        assertThat(firstSideA.questions())
                .extracting(GuideQuestionItem::sequence)
                .containsExactly(1, 2, 3);
        assertThat(firstSideA.questions())
                .extracting(GuideQuestionItem::question)
                .allMatch(question -> question.contains("A측"));
        assertThat(sideB.questions())
                .extracting(GuideQuestionItem::question)
                .allMatch(question -> question.contains("B측"));
        assertThat(sideB).isNotEqualTo(firstSideA);
    }

    @Test
    void createsDeterministicFactSummaryAndArgumentForEachSide() {
        AiRequestContext context = new AiRequestContext(
                "mock-argument-request", "lawyer-argument-v1");
        List<GuideAnswerItem> guideAnswers = List.of(
                new GuideAnswerItem(1, "연락 기준이 있었나요?", "명확한 기준은 없었습니다."),
                new GuideAnswerItem(2, "당시 의도는 무엇이었나요?", "대화를 이어가고 싶었습니다."),
                new GuideAnswerItem(3, "추가로 확인할 점이 있나요?", " ")
        );
        LawyerArgumentRequest sideARequest = new LawyerArgumentRequest(
                10L, TrialSide.A, statement, guideAnswers);
        LawyerArgumentRequest sideBRequest = new LawyerArgumentRequest(
                10L, TrialSide.B, statement, guideAnswers);

        LawyerArgumentResponse firstSideA =
                mockAiClient.createArgumentDraft(context, sideARequest);
        LawyerArgumentResponse secondSideA =
                mockAiClient.createArgumentDraft(context, sideARequest);
        LawyerArgumentResponse sideB =
                mockAiClient.createArgumentDraft(context, sideBRequest);

        assertThat(firstSideA).isEqualTo(secondSideA);
        assertThat(firstSideA.schemaVersion()).isEqualTo("1.0");
        assertThat(firstSideA.factSummary())
                .contains("어제 저녁", "연락 문제", "답장이 늦었습니다", "반복해서 연락했습니다");
        assertThat(firstSideA.argumentText())
                .contains("A측", "명확한 기준은 없었습니다", "대화를 이어가고 싶었습니다");
        assertThat(firstSideA.argumentText())
                .doesNotContain("안내 답변에 따르면")
                .doesNotContain("  ")
                .endsWith(".");
        assertThat(sideB.argumentText()).contains("B측");
        assertThat(sideB).isNotEqualTo(firstSideA);
    }
}
