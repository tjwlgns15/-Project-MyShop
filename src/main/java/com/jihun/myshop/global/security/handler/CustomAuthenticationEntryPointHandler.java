package com.jihun.myshop.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.utils.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // AJAX 요청인지 확인
        if (WebUtil.isAjax(request)) {
            // AJAX 요청인 경우 JSON 응답 반환
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> data = new HashMap<>();
            data.put("redirectUrl", "/auth/login");

            ApiResponseEntity<Object> apiResponse = ApiResponseEntity.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("인증이 필요합니다")
                    .data(data)
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        } else {
            // 일반 요청인 경우 로그인 페이지로 리다이렉트
            response.sendRedirect("/auth/login");
        }
    }
}
