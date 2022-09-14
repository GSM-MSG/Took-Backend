package com.example.took_backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    AUTH_CODE_MISMATCH("인증번호가 일치하지 않습니다,",400),
    TOKEN_EXPIRATION("토큰이 만료 되었습니다.", 401),
    TOKEN_NOT_VALID("토큰이 유효 하지 않습니다.", 401),
    USER_NOT_FOUND("유저를 찾을 수 없습니다.",404),
    CARD_NOT_FOUND("명함을 찾을 수 없습니다.",404),
    MANY_REQUEST_EMAIL_AUTH("15분에 최대 3번 이메일 인증을 요청할 수 있습니다.", 429),
    FAILED_TO_UPLOAD("사진업로드를 실패했습니다",500),
    UNKNOWN("알 수 없는 에러", 500),
    EMAIL_SEND_FAIL("메일 발송에 실패 했습니다",500);
    private final String message;
    private final int status;
}
