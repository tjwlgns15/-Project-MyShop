package com.jihun.myshop.domain.cart.controller.api;

import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto;
import com.jihun.myshop.domain.cart.service.CartToOrderService;
import com.jihun.myshop.domain.order.entity.dto.OrderDto;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart-orders")
public class CartToOrderRestController {

    private final CartToOrderService cartToOrderService;


    @PostMapping
    public ApiResponseEntity<OrderResponseDto> createOrderFromCart(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                   @RequestBody CartOrderDto request) {
        OrderResponseDto response = cartToOrderService.createOrderFromCart(currentUser, request);
        return ApiResponseEntity.success(response);
    }

    @PostMapping("/selected")
    public ApiResponseEntity<OrderResponseDto> createOrderFromSelectedItems(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                            @RequestBody CartOrderSelectDto request) {
        OrderResponseDto response = cartToOrderService.createOrderFromSelectedCartItems(currentUser, request);
        return ApiResponseEntity.success(response);
    }
}
