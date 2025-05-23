package com.jihun.myshop.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jihun.myshop.domain.user.entity.dto.UserDto;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import com.jihun.myshop.global.security.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 권한 정보 확인
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Map<String, String> data = new HashMap<>();

        String redirectUrl;
        if (authorizationService.isAdmin(principal)) {
            redirectUrl = "/admin/dashboard";
        } else {
            redirectUrl = "/orders";
        }
        data.put("redirectUrl", redirectUrl);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponseEntity<Map<String, String>> apiResponse = ApiResponseEntity.success(data);
        objectMapper.writeValue(response.getWriter(), apiResponse);

        clearAuthenticationAttributes(request);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}