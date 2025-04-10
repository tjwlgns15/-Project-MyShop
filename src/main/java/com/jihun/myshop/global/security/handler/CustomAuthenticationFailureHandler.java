package com.jihun.myshop.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponseEntity<?> apiResponse;
        if (exception instanceof BadCredentialsException) {
            apiResponse = ApiResponseEntity.error(ErrorCode.INVALID_CREDENTIALS);
        } else if (exception instanceof UsernameNotFoundException) {
            apiResponse = ApiResponseEntity.error(ErrorCode.USER_NOT_EXIST);
        } else if (exception instanceof CredentialsExpiredException) {
            apiResponse = ApiResponseEntity.error(ErrorCode.CREDENTIALS_EXPIRED);
        } else {
            apiResponse = ApiResponseEntity.error(ErrorCode.AUTHENTICATION_FAILED);
        }

        mapper.writeValue(response.getWriter(), apiResponse);
    }
}