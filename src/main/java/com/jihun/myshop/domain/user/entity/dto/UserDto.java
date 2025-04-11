package com.jihun.myshop.domain.user.entity.dto;

import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCreate {
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
    public static class UserResponse {
        private Long id;
        private String username;
        private String password;
        private String name;
        private String phone;
        private Set<String> userRoles;
        private AddressResponse defaultAddress;
//        private List<OrderResponse> orders;

        public boolean isAdmin() {
            if (this.userRoles != null) {
                for (String role : this.userRoles) {
                    if (role.contains("ROLE_ADMIN")) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

}
