package com.jihun.myshop.domain.user.entity.dto;

import lombok.Data;

@Data
public class UserSignupDto {
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
