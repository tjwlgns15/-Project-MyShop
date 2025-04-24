package com.jihun.myshop.admin.controller.api;

import com.jihun.myshop.domain.user.entity.mapper.UserMapper;
import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserManagementRestController {

    private final UserService userService;


    @GetMapping("/users")
    public ApiResponseEntity<CustomPageResponse<UserResponseDto>> getUsers(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                           CustomPageRequest pageRequest) {
        CustomPageResponse<UserResponseDto> userResponse = userService.getUsers(pageRequest, currentUser);
        return ApiResponseEntity.success(userResponse);
    }

    @PutMapping("/users/roles")
    public ApiResponseEntity<UserResponseDto> updateUserRoles(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                              @RequestBody UserRoleUpdateDto updateDto) {
        UserResponseDto userResponse = userService.updateUserRoles(updateDto, currentUser);
        return ApiResponseEntity.success(userResponse);
    }
}