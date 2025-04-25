package com.jihun.myshop.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getStatus())
                .code(e.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    // 400 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(SC_BAD_REQUEST)
                .code("X002") // 유효성 검사 실패에 대한 코드 추가
                .message("입력 값이 유효하지 않습니다: " + e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();

        return ResponseEntity.status(SC_BAD_REQUEST).body(response);
    }

    // 500 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(SC_INTERNAL_SERVER_ERROR)
                .code("X999") // 일반적인 서버 오류에 대한 코드 추가
                .message("서버 내부 오류가 발생했습니다: " + e.getMessage())
                .build();

        return ResponseEntity.status(SC_INTERNAL_SERVER_ERROR).body(response);
    }
}