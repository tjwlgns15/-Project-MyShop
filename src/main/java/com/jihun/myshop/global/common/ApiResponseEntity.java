package com.jihun.myshop.global.common;

import com.jihun.myshop.global.exception.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseEntity<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponseEntity<T> success(T data) {
        return new ApiResponseEntity<T>(200, "success", data);
    }

    public static <T> ApiResponseEntity<T> success(T data, String message) {
        return new ApiResponseEntity<T>(200, message, data);
    }
    public static <T> ApiResponseEntity<T> success(String message) {
        return new ApiResponseEntity<T>(200, message, null);
    }

    public static <T> ApiResponseEntity<T> error(ErrorCode errorCode) {
        return new ApiResponseEntity<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

}
