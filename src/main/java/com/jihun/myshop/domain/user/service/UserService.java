package com.jihun.myshop.domain.user.service;

import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.domain.user.entity.Role;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.mapper.UserMapper;
import com.jihun.myshop.domain.user.repository.RoleRepository;
import com.jihun.myshop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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


}
