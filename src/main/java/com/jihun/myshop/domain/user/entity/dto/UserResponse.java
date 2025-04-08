package com.jihun.myshop.domain.user.entity.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private Set<String> userRoles;
    private AddressDto defaultAddress;

//    private List<OrderResponse> orders;


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
