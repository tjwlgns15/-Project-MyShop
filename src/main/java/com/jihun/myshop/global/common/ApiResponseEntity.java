package com.jihun.myshop.global.common;

import com.jihun.myshop.global.exception.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseEntity<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponseEntity<T> success(T data) {
        return new ApiResponseEntity<T>(OK.value(), "success", data);
    }

    public static <T> ApiResponseEntity<T> success(T data, String message) {
        return new ApiResponseEntity<T>(OK.value(), message, data);
    }
    public static <T> ApiResponseEntity<T> success(String message) {
        return new ApiResponseEntity<T>(OK.value(), message, null);
    }

    public static <T> ApiResponseEntity<T> error(ErrorCode errorCode) {
        return new ApiResponseEntity<T>(errorCode.getStatus(), errorCode.getMessage(), null);
    }
    public static <T> ApiResponseEntity<T> error(String message) {
        return new ApiResponseEntity<T>(INTERNAL_SERVER_ERROR.value(), message, null);
    }

}
