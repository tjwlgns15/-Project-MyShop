package com.jihun.myshop.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int status;
    private final String code;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    public CustomException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }
}