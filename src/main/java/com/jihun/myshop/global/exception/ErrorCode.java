package com.jihun.myshop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_USERNAME(400, "이미 사용중인계정입니다."),
    ROLE_NOT_FOUND(400, "등록되지 않은 권한 정보입니다."),

    USER_NOT_FOUND(400, "회원 정보가 일치하지 않습니다.")


    ;
    private final int code;
    private final String message;
}
