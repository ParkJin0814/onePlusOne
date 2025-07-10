package com.example.oneplusone.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 관련 에러 (401 Unauthorized)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "권한이 부족합니다"),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    USERROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 UserRole"),
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용중인 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품를 찾을 수 없습니다"),
    PRODUCT_IS_NOT_YOURS(HttpStatus.FORBIDDEN, "본인의 상품이 아닙니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "상품 수량이 부족합니다"),

    // 입력값 검증 에러 (400 Bad Request)
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다"),

    // 서버 오류 (500 Internal Server Error)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다")

    ;

    private final HttpStatus status;// HTTP 상태 코드
    private final String message;// 에러 메시지

}
