package com.jihun.myshop.domain.user.entity.dto;

import lombok.*;

public class AddressDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressCreate {
        private String recipientName;
        private String zipCode;
        private String address1;
        private String address2;
        private String phone;
        private boolean isDefault;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressResponse {
        private Long id;
        private String recipientName;
        private String zipCode;
        private String address1;
        private String address2;
        private String phone;
        private boolean isDefault;
    }

}