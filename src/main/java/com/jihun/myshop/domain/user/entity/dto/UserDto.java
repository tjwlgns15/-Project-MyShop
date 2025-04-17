package com.jihun.myshop.domain.user.entity.dto;

import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCreateDto {
        private String username;
        private String password;
        private String name;
        private String phone;

        private String recipientName;
        private String zipCode;
        private String address1;
        private String address2;
        private String addressPhone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponseDto {
        private Long id;
        private String username;
        private String password;
        private String name;
        private String phone;
        private Set<String> userRoles;
        private AddressResponseDto defaultAddress;
//        private List<OrderResponseDto> orders;
//        private CartResponseDto cart;
//        private List<ReviewResponseDto> reviews;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoUpdateDto {
        private String name;
        private String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordChangeDto {
        private String currentPassword;
        private String newPassword;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRoleUpdateDto {
        private Long userId;
        private List<String> roleNames;
    }

}
