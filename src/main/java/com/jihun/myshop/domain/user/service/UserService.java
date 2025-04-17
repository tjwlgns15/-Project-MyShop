package com.jihun.myshop.domain.user.service;

import com.jihun.myshop.domain.user.entity.dto.UserDto;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.domain.user.entity.Role;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.mapper.UserMapper;
import com.jihun.myshop.domain.user.repository.RoleRepository;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import com.jihun.myshop.global.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final AuthorizationService authorizationService;


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }


    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException(DUPLICATE_USERNAME);
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new CustomException(ROLE_NOT_FOUND));

        User newUser = userMapper.fromCreateDto(dto, passwordEncoder);

        newUser.updatePassword(passwordEncoder.encode(dto.getPassword()));
        newUser.addRole(userRole);

        User saveUser = userRepository.save(newUser);
        return userMapper.fromEntity(saveUser);
    }

    public UserResponseDto getProfile(CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());
        return userMapper.fromEntity(user);
    }

    @Transactional
    public UserResponseDto changePassword(PasswordChangeDto changeDto, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());

        if (!passwordEncoder.matches(changeDto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(INVALID_CREDENTIALS);
        }

        user.updatePassword(passwordEncoder.encode(changeDto.getNewPassword()));
        return userMapper.fromEntity(user);
    }

    @Transactional
    public UserResponseDto updateProfile(UserInfoUpdateDto updateDto, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());

        // 입력된 정보만 업데이트 (null 체크)
        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            user.updateName(updateDto.getName());
        }

        if (updateDto.getPhone() != null && !updateDto.getPhone().isBlank()) {
            user.updatePhone(updateDto.getPhone());
        }

        return userMapper.fromEntity(user);
    }

    public PageResponse<UserResponseDto> getUsers(CustomPageRequest pageRequest, CustomUserDetails currentUser) {
        authorizationService.validateAdmin(currentUser);

        Page<User> allUsers = userRepository.findAllUsers(pageRequest.toPageRequest());
        Page<UserResponseDto> userResponsePage = allUsers.map(userMapper::fromEntity);

        return PageResponse.fromPage(userResponsePage);
    }

    @Transactional
    public UserResponseDto updateUserRoles(UserRoleUpdateDto updateDto, CustomUserDetails currentUser) {
        authorizationService.validateAdmin(currentUser);

        User user = getUserById(updateDto.getUserId());

        user.clearRoles();

        for (String roleName : updateDto.getRoleNames()) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new CustomException(ROLE_NOT_FOUND));
            user.addRole(role);
        }

        return userMapper.fromEntity(user);
    }
}
