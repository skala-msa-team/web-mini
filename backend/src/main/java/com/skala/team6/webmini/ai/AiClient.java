package com.skala.team6.webmini.ai;

public interface AiClient {

    LawyerQuestionsResponse createGuideQuestions(AiRequestContext context, LawyerQuestionsRequest request);

    LawyerArgumentResponse createArgumentDraft(AiRequestContext context, LawyerArgumentRequest request);

    JudgeVerdictResponse createVerdict(AiRequestContext context, JudgeVerdictRequest request);
}
