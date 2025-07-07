package com.example.oneplusone.global.exception;

import com.example.oneplusone.domain.common.dto.ApiResponse;
import com.example.oneplusone.domain.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외(BaseException) 처리
     * 서비스, 도메인 계층 등에서 발생한 비즈니스 예외를 처리합니다.
     *
     * @param e BaseException (커스텀 예외 클래스)
     * @return 공통응답객체를 통한 일괄적인 응답형식
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handlePlannerException(BaseException e) {
        ApiResponse<?> response = ApiResponse.error(e.getMessage());
        log.error("커스텀 예외처리 발생", e);
        return new ResponseEntity<>(response, e.getStatus());
    }

    /**
     * Valid 어노테이션 검증 실패 시 발생하는 예외 처리
     *
     * @param ex MethodArgumentNotValidException
     * @return 공통응답객체의 오류 메시지를 포함한 응답 (HTTP 400 Bad Request)
     * 첫 번째 예외메세지만 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex){


        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("유효성 검사에 실패했습니다");

        ApiResponse<?> response = ApiResponse.error(message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * IllegalArgumentException 처리
     * 주로 잘못된 파라미터나 조건 위반 등에서 발생
     *
     * @param e IllegalArgumentException
     * @return 공통응답객체를 통한 일괄적인 응답형식  (HTTP 400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse<?> response = ApiResponse.error("잘못된 요청 형식입니다.");
        log.error("잘못된 요청 형식 발생 ", e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception 처리
     * 서버오류를 처리
     *
     * @param e Exception
     * @return 공통응답객체를 통한 일괄적인 응답형식
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        ApiResponse<?> response = ApiResponse.error("처리 중 오류가 발생했습니다");
        log.error("서버오류 발생", e);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
