package com.jihun.myshop.domain.user.entity.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String recipientName;
    private String zipCode;
    private String address1;
    private String address2;
    private String phone;
    private boolean isDefault;
}