package com.jihun.myshop.domain.user.controller.api;

import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.UserCreate;
import static com.jihun.myshop.domain.user.entity.dto.UserDto.UserResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponseEntity<UserResponse> signup(@RequestBody UserCreate dto) {
        UserResponse userResponse = userService.createUser(dto);
        return ApiResponseEntity.success(userResponse);
    }


}
