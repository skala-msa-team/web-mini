package com.skala.team6.webmini.ai;

import com.skala.team6.webmini.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Mock AI", description = "Backend 내부 Mock AI Adapter 확인용 컨트롤러")
@RestController
@RequestMapping("/api/v1/mock-ai")
public class MockAiController {

    private final AiClient aiClient;

    public MockAiController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @Operation(summary = "안내 질문 생성")
    @PostMapping("/lawyer/questions")
    public ResponseEntity<ApiResponse<LawyerQuestionsResponse>> createGuideQuestions(
            @Valid @RequestBody LawyerQuestionsRequest request
    ) {
        AiRequestContext context = new AiRequestContext(UUID.randomUUID().toString(), "lawyer-questions-v1");
        return ResponseEntity.ok(ApiResponse.of(aiClient.createGuideQuestions(context, request)));
    }

    @Operation(summary = "변론문 초안 생성")
    @PostMapping("/lawyer/argument")
    public ResponseEntity<ApiResponse<LawyerArgumentResponse>> createArgumentDraft(
            @Valid @RequestBody LawyerArgumentRequest request
    ) {
        AiRequestContext context = new AiRequestContext(UUID.randomUUID().toString(), "lawyer-argument-v1");
        return ResponseEntity.ok(ApiResponse.of(aiClient.createArgumentDraft(context, request)));
    }

    @Operation(summary = "판결 생성")
    @PostMapping("/judge/verdict")
    public ResponseEntity<ApiResponse<JudgeVerdictResponse>> createVerdict(
            @Valid @RequestBody JudgeVerdictRequest request
    ) {
        AiRequestContext context = new AiRequestContext(UUID.randomUUID().toString(), request.promptVersion());
        return ResponseEntity.ok(ApiResponse.of(aiClient.createVerdict(context, request)));
    }
}
