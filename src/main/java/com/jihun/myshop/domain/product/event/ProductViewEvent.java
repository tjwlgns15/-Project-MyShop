package com.jihun.myshop.domain.product.event;

import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductViewEvent {
    private final CustomUserDetails user;
    private final Long productId;
}