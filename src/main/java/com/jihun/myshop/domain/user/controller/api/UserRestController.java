package com.jihun.myshop.domain.user.controller.api;

import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    /*
    이름, 권한 등의 간단한 정보만 표시
    주문 목록, 장바구니 목록 등의 항목 표시
     */
    @GetMapping("/profile")
    public ApiResponseEntity<UserResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
        UserResponseDto userResponse = userService.getProfile(currentUser);
        return ApiResponseEntity.success(userResponse);
    }

    @PutMapping("/change-password")
    public ApiResponseEntity<UserResponseDto> changePassword(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                             @RequestBody PasswordChangeDto changeDto) {
        UserResponseDto userResponse = userService.changePassword(changeDto, currentUser);
        return ApiResponseEntity.success(userResponse);
    }

    @PutMapping("/profile")
    public ApiResponseEntity<UserResponseDto> updateProfile(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                            @RequestBody UserInfoUpdateDto updateDto) {
        UserResponseDto userResponse = userService.updateProfile(updateDto, currentUser);
        return ApiResponseEntity.success(userResponse);
    }

}
