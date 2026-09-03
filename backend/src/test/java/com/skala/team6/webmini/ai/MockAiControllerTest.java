package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.config.AppAiProperties;
import com.skala.team6.webmini.common.exception.GlobalExceptionHandler;
import com.skala.team6.webmini.common.model.RelationshipType;
import com.skala.team6.webmini.common.model.TrialSide;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MockAiControllerTest {

    private final CapturingAiClient aiClient = new CapturingAiClient();
    private final MockAiController controller = new MockAiController(aiClient);
    private final StatementPayload statement = new StatementPayload(
            "어제 저녁",
            "연락 문제로 다투었습니다.",
            "답장이 늦었습니다.",
            "반복해서 연락했습니다.",
            "감정이 상한 채 대화가 끝났습니다.",
            "연락 기준을 합의하고 싶습니다."
    );

    @Test
    void passesRequestIdAndQuestionsPromptVersionToAiClient() {
        controller.createGuideQuestions(new LawyerQuestionsRequest(
                10L, TrialSide.A, RelationshipType.COUPLE, statement));

        assertThatCodeIsUuid(aiClient.context.aiRequestId());
        assertThat(aiClient.context.promptVersion()).isEqualTo("lawyer-questions-v1");
    }

    @Test
    void passesRequestIdAndArgumentPromptVersionToAiClient() {
        controller.createArgumentDraft(new LawyerArgumentRequest(
                10L,
                TrialSide.B,
                statement,
                List.of(new GuideAnswerItem(1, "질문", "답변"))
        ));

        assertThatCodeIsUuid(aiClient.context.aiRequestId());
        assertThat(aiClient.context.promptVersion()).isEqualTo("lawyer-argument-v1");
    }

    @Test
    void returnsQuestionsUsingApprovedJsonSchema() throws Exception {
        MockMvc mockMvc = mockMvcWithRealMockClient();

        mockMvc.perform(post("/api/v1/mock-ai/lawyer/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trialId": 10,
                                  "side": "A",
                                  "relationshipType": "COUPLE",
                                  "statement": {
                                    "incidentTime": "어제 저녁",
                                    "situation": "연락 문제로 다투었습니다.",
                                    "counterpartAction": "답장이 늦었습니다.",
                                    "ownAction": "반복해서 연락했습니다.",
                                    "afterConversation": "감정이 상한 채 대화가 끝났습니다.",
                                    "desiredResolution": "연락 기준을 합의하고 싶습니다."
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions.length()").value(3))
                .andExpect(jsonPath("$.data.questions[0].sequence").value(1))
                .andExpect(jsonPath("$.data.questions[0].question").value(
                        org.hamcrest.Matchers.containsString("A측")))
                .andExpect(jsonPath("$.data.schemaVersion").value("1.0"));
    }

    @Test
    void returnsArgumentUsingApprovedJsonSchema() throws Exception {
        MockMvc mockMvc = mockMvcWithRealMockClient();

        mockMvc.perform(post("/api/v1/mock-ai/lawyer/argument")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trialId": 10,
                                  "side": "B",
                                  "statement": {
                                    "incidentTime": "어제 저녁",
                                    "situation": "연락 문제로 다투었습니다.",
                                    "counterpartAction": "연락을 반복해서 받았습니다.",
                                    "ownAction": "답장을 미뤘습니다.",
                                    "afterConversation": "대화를 중단했습니다.",
                                    "desiredResolution": "연락 시간을 합의하고 싶습니다."
                                  },
                                  "guideAnswers": [
                                    {
                                      "sequence": 1,
                                      "question": "연락 기준이 있었나요?",
                                      "answer": "명확한 기준은 없었습니다."
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.factSummary").isString())
                .andExpect(jsonPath("$.data.argumentText").value(
                        org.hamcrest.Matchers.containsString("B측")))
                .andExpect(jsonPath("$.data.schemaVersion").value("1.0"));
    }

    @Test
    void rejectsInvalidQuestionsRequest() throws Exception {
        MockMvc mockMvc = mockMvcWithRealMockClient();

        mockMvc.perform(post("/api/v1/mock-ai/lawyer/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trialId": 10,
                                  "side": "A",
                                  "relationshipType": "COUPLE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private MockMvc mockMvcWithRealMockClient() {
        MockAiClient realMockClient =
                new MockAiClient(new AppAiProperties("mock", "judge-v1"));
        return MockMvcBuilders.standaloneSetup(new MockAiController(realMockClient))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void assertThatCodeIsUuid(String value) {
        assertThat(UUID.fromString(value).toString()).isEqualTo(value);
    }

    private static final class CapturingAiClient implements AiClient {
        private AiRequestContext context;

        @Override
        public LawyerQuestionsResponse createGuideQuestions(
                AiRequestContext context,
                LawyerQuestionsRequest request
        ) {
            this.context = context;
            return new LawyerQuestionsResponse(List.of(), "1.0");
        }

        @Override
        public LawyerArgumentResponse createArgumentDraft(
                AiRequestContext context,
                LawyerArgumentRequest request
        ) {
            this.context = context;
            return new LawyerArgumentResponse("요약", "변론", "1.0");
        }

        @Override
        public JudgeVerdictResponse createVerdict(
                AiRequestContext context,
                JudgeVerdictRequest request
        ) {
            throw new UnsupportedOperationException();
        }
    }
}
