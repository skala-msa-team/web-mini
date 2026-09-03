package com.skala.team6.webmini.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값을 확인해 주세요."),
    DEMO_USER_REQUIRED(HttpStatus.UNAUTHORIZED, "Demo 사용자 식별값이 필요합니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    TRIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "재판을 찾을 수 없습니다."),
    TRIAL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 재판이 생성되었습니다."),
    TRIAL_NOT_PREPARING(HttpStatus.CONFLICT, "준비 단계에서만 수행할 수 있습니다."),
    INVALID_TRIAL_SIDE(HttpStatus.BAD_REQUEST, "유효하지 않은 측 정보입니다."),
    GUIDE_ANSWER_REQUIRED(HttpStatus.BAD_REQUEST, "안내 질문 답변이 필요합니다."),
    GUIDE_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "안내 질문을 찾을 수 없습니다."),
    GUIDE_ANSWERS_INCOMPLETE(HttpStatus.CONFLICT, "모든 안내 질문 답변이 완료되지 않았습니다."),
    ARGUMENT_DRAFT_REQUIRED(HttpStatus.CONFLICT, "변론문 초안이 필요합니다."),
    PARTIES_NOT_READY(HttpStatus.CONFLICT, "양측 준비가 완료되지 않았습니다."),
    TRIAL_ALREADY_STARTED(HttpStatus.CONFLICT, "이미 시작된 재판입니다."),
    CHAT_NOT_ALLOWED(HttpStatus.CONFLICT, "현재 채팅이 허용되지 않습니다."),
    MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "채팅 글자 수 제한을 초과했습니다."),
    VOTING_NOT_OPEN(HttpStatus.CONFLICT, "현재 투표가 열려 있지 않습니다."),
    ALREADY_VOTED(HttpStatus.CONFLICT, "이미 투표했습니다."),
    TRIAL_ALREADY_ENDED(HttpStatus.CONFLICT, "이미 종료된 재판입니다."),
    RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "결과를 찾을 수 없습니다."),
    MOCK_AI_RESPONSE_INVALID(HttpStatus.UNPROCESSABLE_ENTITY, "Mock AI 응답 검증에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
