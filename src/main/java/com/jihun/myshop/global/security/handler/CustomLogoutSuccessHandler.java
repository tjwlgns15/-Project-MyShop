package com.jihun.myshop.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.myshop.global.common.ApiResponseEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final  ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Map<String, String> data = new HashMap<>();
        String redirectUrl = "/auth/login";
        data.put("redirectUrl", redirectUrl);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponseEntity<Map<String, String>> apiResponse = ApiResponseEntity.success(data, "로그아웃이 완료되었습니다.");
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
