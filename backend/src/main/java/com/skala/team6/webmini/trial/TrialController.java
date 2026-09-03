package com.skala.team6.webmini.trial;

import com.skala.team6.webmini.common.api.ApiResponse;
import com.skala.team6.webmini.common.model.TrialSide;
import com.skala.team6.webmini.common.model.TrialStatus;
import com.skala.team6.webmini.common.model.Visibility;
import com.skala.team6.webmini.demo.DemoUserContext;
import com.skala.team6.webmini.demo.DemoUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Trial", description = "재판 진행 API")
@Validated
@RestController
@RequestMapping("/api/v1/trials")
public class TrialController {
    private final TrialQueryService trialQueryService;
    private final TrialStatementService trialStatementService;
    private final GuideAnswerService guideAnswerService;
    private final TrialArgumentService trialArgumentService;

    public TrialController(
            TrialQueryService trialQueryService,
            TrialStatementService trialStatementService,
            GuideAnswerService guideAnswerService,
            TrialArgumentService trialArgumentService
    ) {
        this.trialQueryService = trialQueryService;
        this.trialStatementService = trialStatementService;
        this.guideAnswerService = guideAnswerService;
        this.trialArgumentService = trialArgumentService;
    }

    @Operation(summary = "재판 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<TrialListResponse>> getTrials(
            @RequestParam(required = false) TrialStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        TrialListResponse response = new TrialListResponse(
                List.of(new TrialListItem(
                        21L,
                        status == null ? TrialStatus.PREPARING : status,
                        "연락 문제로 다툰 사연",
                        "A측",
                        "B측",
                        Visibility.PUBLIC
                )),
                page,
                size,
                1,
                1
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "재판 기본 정보 조회")
    @GetMapping("/{trialId}")
    public ResponseEntity<ApiResponse<TrialDetailResponse>> getTrial(
            @PathVariable @Min(1) Long trialId
    ) {
        TrialQueryService.TrialDetail detail = trialQueryService.findDetail(trialId);
        var trial = detail.trial();
        var post = trial.getPost();
        var parties = detail.parties();
        TrialDetailResponse response = new TrialDetailResponse(
                trial.getId(),
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getRelationshipType(),
                trial.getVisibility(),
                trial.getStatus(),
                trial.getPhaseStartedAt() == null ? null : trial.getPhaseStartedAt().toString(),
                trial.getPhaseEndsAt() == null ? null : trial.getPhaseEndsAt().toString(),
                new TrialPartyInfo(parties.get(0).getSide(), parties.get(0).getDisplayName()),
                new TrialPartyInfo(parties.get(1).getSide(), parties.get(1).getDisplayName())
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "당사자 진술 저장")
    @PutMapping("/{trialId}/parties/{side}/statement")
    public ResponseEntity<ApiResponse<StatementResponse>> saveStatement(
            @PathVariable @Min(1) Long trialId,
            @PathVariable TrialSide side,
            @Parameter(
                    name = "X-Demo-User-Id",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "브라우저 Local Storage Demo 사용자 UUID"
            )
            @DemoUserId DemoUserContext demoUser,
            @Valid @RequestBody StatementRequest request
    ) {
        var statement = trialStatementService.save(trialId, side, request);
        StatementRequest saved = new StatementRequest(
                statement.getIncidentTime(),
                statement.getSituation(),
                statement.getCounterpartAction(),
                statement.getOwnAction(),
                statement.getAfterConversation(),
                statement.getDesiredResolution()
        );
        return ResponseEntity.ok(ApiResponse.of(new StatementResponse(side, saved)));
    }

    @Operation(summary = "안내 질문 생성")
    @PostMapping("/{trialId}/parties/{side}/guide-questions")
    public ResponseEntity<ApiResponse<GuideQuestionsResponse>> createGuideQuestions(
            @PathVariable @Min(1) Long trialId,
            @PathVariable TrialSide side
    ) {
        GuideQuestionsResponse response = new GuideQuestionsResponse(
                101L,
                1,
                List.of("평소 두 분이 합의한 연락 기준이 있었나요?")
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "안내 질문 답변 저장")
    @PutMapping("/{trialId}/parties/{side}/guide-answers")
    public ResponseEntity<ApiResponse<GuideAnswersResponse>> saveGuideAnswers(
            @PathVariable @Min(1) Long trialId,
            @PathVariable TrialSide side,
            @Parameter(
                    name = "X-Demo-User-Id",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "브라우저 Local Storage Demo 사용자 UUID"
            )
            @DemoUserId DemoUserContext demoUser,
            @Valid @RequestBody GuideAnswersRequest request
    ) {
        GuideAnswerService.SavedGuideAnswers saved = guideAnswerService.save(trialId, side, request);
        GuideAnswersResponse response = new GuideAnswersResponse(saved.answers(), saved.allAnswered());
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "변론문 초안 생성")
    @PostMapping("/{trialId}/parties/{side}/argument-draft")
    public ResponseEntity<ApiResponse<ArgumentDraftResponse>> createArgumentDraft(
            @PathVariable @Min(1) Long trialId,
            @PathVariable TrialSide side
    ) {
        return ResponseEntity.ok(ApiResponse.of(new ArgumentDraftResponse(
                side,
                "양측은 연락 빈도에 대한 명확한 합의가 없었습니다.",
                "A측은 불안감 때문에 반복 연락했으나 사전 합의가 없었다고 주장합니다."
        )));
    }

    @Operation(summary = "변론문 초안 수정")
    @PutMapping("/{trialId}/parties/{side}/argument-draft")
    public ResponseEntity<ApiResponse<ArgumentDraftResponse>> updateArgumentDraft(
            @PathVariable @Min(1) Long trialId,
            @PathVariable TrialSide side,
            @Parameter(
                    name = "X-Demo-User-Id",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "브라우저 Local Storage Demo 사용자 UUID"
            )
            @DemoUserId DemoUserContext demoUser,
            @Valid @RequestBody UpdateArgumentDraftRequest request
    ) {
        var statement = trialArgumentService.updateDraft(trialId, side, request);
        return ResponseEntity.ok(ApiResponse.of(new ArgumentDraftResponse(
                side, statement.getFactSummary(), statement.getArgumentText())));
    }

    @Operation(summary = "변론문 최종 확인")
    @PostMapping("/{trialId}/parties/{side}/confirm")
    public ResponseEntity<ApiResponse<ConfirmArgumentResponse>> confirmArgument(
            @PathVariable @Min(1) Long trialId,
            @PathVariable TrialSide side,
            @Parameter(
                    name = "X-Demo-User-Id",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "브라우저 Local Storage Demo 사용자 UUID"
            )
            @DemoUserId DemoUserContext demoUser
    ) {
        return ResponseEntity.ok(ApiResponse.of(new ConfirmArgumentResponse(
                side,
                "2026-09-03T03:20:00Z",
                side == TrialSide.B
        )));
    }

    @Operation(summary = "재판 시작")
    @PostMapping("/{trialId}/start")
    public ResponseEntity<ApiResponse<TrialSnapshotResponse>> startTrial(
            @PathVariable @Min(1) Long trialId,
            @Parameter(
                    name = "X-Demo-User-Id",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "브라우저 Local Storage Demo 사용자 UUID"
            )
            @DemoUserId DemoUserContext demoUser
    ) {
        return ResponseEntity.ok(ApiResponse.of(sampleSnapshot(TrialStatus.INTRODUCTION)));
    }

    @Operation(summary = "현재 상태 스냅샷 조회")
    @GetMapping("/{trialId}/snapshot")
    public ResponseEntity<ApiResponse<TrialSnapshotResponse>> getSnapshot(
            @PathVariable @Min(1) Long trialId
    ) {
        return ResponseEntity.ok(ApiResponse.of(sampleSnapshot(TrialStatus.INTRODUCTION)));
    }

    @Operation(summary = "재판 이벤트 조회")
    @GetMapping("/{trialId}/events")
    public ResponseEntity<ApiResponse<List<TrialEventResponse>>> getEvents(
            @PathVariable @Min(1) Long trialId,
            @RequestParam(defaultValue = "0") @Min(0) long afterSequence
    ) {
        List<TrialEventResponse> response = List.of(
                new TrialEventResponse(1, "TRIAL_STARTED", "{\"status\":\"INTRODUCTION\"}", "2026-09-03T03:00:00Z")
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "이전 채팅 조회")
    @GetMapping("/{trialId}/messages")
    public ResponseEntity<ApiResponse<TrialMessagesResponse>> getMessages(
            @PathVariable @Min(1) Long trialId,
            @RequestParam(defaultValue = "0") @Min(0) long afterSequence,
            @RequestParam(defaultValue = "100") @Min(1) @Max(200) int size
    ) {
        TrialMessagesResponse response = new TrialMessagesResponse(
                List.of(new TrialMessageItem(
                        9001L,
                        15,
                        trialId,
                        new TrialMessageSender(
                                "7f33baf1-74aa-4eaf-8750-139f6324784f",
                                "관전자1"
                        ),
                        "A측의 설명도 이해됩니다.",
                        "2026-09-03T03:05:00Z"
                )),
                15,
                false
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "승소 투표")
    @PostMapping("/{trialId}/votes")
    public ResponseEntity<ApiResponse<VoteResponse>> vote(
            @PathVariable @Min(1) Long trialId,
            @Parameter(
                    name = "X-Demo-User-Id",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "브라우저 Local Storage Demo 사용자 UUID"
            )
            @DemoUserId DemoUserContext demoUser,
            @Valid @RequestBody VoteRequest request
    ) {
        return ResponseEntity.status(201).body(ApiResponse.of(new VoteResponse(
                request.selectedSide(),
                "2026-09-03T03:25:00Z"
        )));
    }

    @Operation(summary = "재판 결과 조회")
    @GetMapping("/{trialId}/results")
    public ResponseEntity<ApiResponse<TrialResultResponse>> getResults(
            @PathVariable @Min(1) Long trialId
    ) {
        TrialResultResponse response = new TrialResultResponse(
                trialId,
                new VerdictPayload(
                        TrialSide.B,
                        60,
                        40,
                        "판결 요지",
                        List.of("판단 근거"),
                        "A측 개선 행동",
                        "B측 개선 행동"
                ),
                new PublicVotePayload(7, 13, 20)
        );
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    private TrialSnapshotResponse sampleSnapshot(TrialStatus status) {
        return new TrialSnapshotResponse(
                status,
                "2026-09-03T03:00:00Z",
                "2026-09-03T03:10:00Z",
                "2026-09-03T03:30:00Z",
                12,
                15,
                true,
                false
        );
    }
}
