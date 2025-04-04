package com.jihun.myshop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EXAMPLE(400, "Ex")


    ;
    private final int code;
    private final String message;
}
