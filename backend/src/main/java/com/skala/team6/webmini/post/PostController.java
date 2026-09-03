package com.skala.team6.webmini.post;

import com.skala.team6.webmini.common.api.ApiResponse;
import com.skala.team6.webmini.database.entity.PostEntity;
import com.skala.team6.webmini.demo.DemoUserContext;
import com.skala.team6.webmini.demo.DemoUserId;
import com.skala.team6.webmini.trial.TrialCreationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Post", description = "게시글과 재판 생성 API")
@Validated
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final TrialCreationService trialCreationService;

    public PostController(PostService postService, TrialCreationService trialCreationService) {
        this.postService = postService;
        this.trialCreationService = trialCreationService;
    }

    @Operation(summary = "갈등 게시글 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePostResponse>> createPost(
            @DemoUserId DemoUserContext demoUser,
            @Valid @RequestBody CreatePostRequest request
    ) {
        PostEntity post = postService.createPost(demoUser.demoUserId(), request);
        CreatePostResponse response = new CreatePostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getRelationshipType(),
                post.isTrialRequested()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @Operation(summary = "공개 재판 생성")
    @PostMapping("/{postId}/trials")
    public ResponseEntity<ApiResponse<CreateTrialResponse>> createTrial(
            @PathVariable @Min(1) Long postId,
            @DemoUserId DemoUserContext demoUser,
            @Valid @RequestBody CreateTrialRequest request
    ) {
        TrialCreationService.CreatedTrial created = trialCreationService.create(
                demoUser.demoUserId(), postId, request.aDisplayName(), request.bDisplayName());
        CreateTrialResponse response = new CreateTrialResponse(
                created.trial().getId(),
                created.trial().getStatus(),
                new TrialPartySummary("A", created.parties().get(0).getDisplayName()),
                new TrialPartySummary("B", created.parties().get(1).getDisplayName())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }
}
