package com.jihun.myshop.domain.user.controller;

import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.domain.user.entity.dto.UserResponse;
import com.jihun.myshop.domain.user.entity.dto.UserSignupDto;
import com.jihun.myshop.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponseEntity<UserResponse> signup(@RequestBody UserSignupDto dto) {
        UserResponse userResponse = userService.createUser(dto);
        return ApiResponseEntity.success(userResponse);
    }


}
