package com.jihun.myshop.domain.user.controller.api;

import com.jihun.myshop.domain.user.entity.dto.UserDto;
import com.jihun.myshop.domain.user.entity.dto.UserDto.UserResponseDto;
import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.UserCreateDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponseEntity<UserResponseDto> signup(@RequestBody UserCreateDto dto) {
        UserResponseDto userResponse = userService.createUser(dto);
        return ApiResponseEntity.success(userResponse);
    }






}
