package com.jihun.myshop.domain.cart.controller.api;

import com.jihun.myshop.domain.cart.entity.dto.CartDto.CartResponseDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemCreateDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemUpdateDto;
import com.jihun.myshop.domain.cart.entity.mapper.CartMapper;
import com.jihun.myshop.domain.cart.service.CartService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartRestController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    /**
     * 장바구니에 상품 추가
     */
    @PostMapping("/items")
    public ApiResponseEntity<CartResponseDto> addItemToCart(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                            @RequestBody CartItemCreateDto requestDto) {
        CartResponseDto cartResponseDto = cartService.addItemToCart(currentUser, requestDto);
        return ApiResponseEntity.success(cartResponseDto);
    }

    /**
     * 현재 사용자의 장바구니 조회
     */
    @GetMapping
    public ApiResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal CustomUserDetails currentUser) {
        CartResponseDto cartResponseDto = cartService.getCart(currentUser);
        return ApiResponseEntity.success(cartResponseDto);
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @PutMapping("/items")
    public ApiResponseEntity<CartResponseDto> updateCartItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                             @Valid @RequestBody CartItemUpdateDto requestDto) {
        CartResponseDto cartResponseDto = cartService.updateCartItem(currentUser, requestDto);
        return ApiResponseEntity.success(cartResponseDto);
    }

    /**
     * 장바구니에서 상품 제거
     */
    @DeleteMapping("/items/{cartItemId}")
    public ApiResponseEntity<CartResponseDto> removeCartItem(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                             @PathVariable Long cartItemId) {
        CartResponseDto cartResponseDto = cartService.removeCartItem(currentUser, cartItemId);
        return ApiResponseEntity.success(cartResponseDto);
    }

    /**
     * 장바구니 비우기
     */
    @DeleteMapping
    public ApiResponseEntity<CartResponseDto> clearCart(@AuthenticationPrincipal CustomUserDetails currentUser) {
        CartResponseDto cartResponseDto = cartService.clearCart(currentUser);
        return ApiResponseEntity.success(cartResponseDto);
    }
}
