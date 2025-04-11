package com.jihun.myshop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_USERNAME(400, "이미 사용중인계정입니다."),
    ROLE_NOT_FOUND(404, "등록되지 않은 권한 정보입니다."),
    INVALID_CREDENTIALS(401, "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_EXIST(400, "회원 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(403, "접근 권한이 없습니다."),
    ACCOUNT_DISABLED(401, "비활성화된 계정입니다."),
    ACCOUNT_LOCKED(401, "계정이 잠겼습니다."),
    CREDENTIALS_EXPIRED(401, "자격 증명이 만료되었습니다"),
    AUTHENTICATION_FAILED(401, "인증에 실패하였습니다."),

    CATEGORY_NOT_EXIST(404, "카테고리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "상품 정보를 찾을 수 없습니다."),
    OUT_OF_STOCK(400, "재고량이 부족합니다."),

    BED_REQUEST(400, "잘못된 설정입니다.")


    ;
    private final int code;
    private final String message;
}

