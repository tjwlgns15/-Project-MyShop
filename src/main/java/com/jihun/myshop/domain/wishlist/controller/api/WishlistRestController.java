package com.jihun.myshop.domain.wishlist.controller.api;

import com.jihun.myshop.domain.wishlist.entity.dto.WishlistItemDto.WishlistResponseDto;
import com.jihun.myshop.domain.wishlist.service.WishlistService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.jihun.myshop.domain.wishlist.entity.dto.WishlistItemDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistRestController {

    private final WishlistService wishlistService;


    @GetMapping
    public ApiResponseEntity<PageResponse<WishlistResponseDto>> getWishlistItems(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                 CustomPageRequest pageRequest) {
        PageResponse<WishlistResponseDto> responseDto = wishlistService.getWishlistItems(currentUser, pageRequest);
        return ApiResponseEntity.success(responseDto);
    }

    @PostMapping
    public ApiResponseEntity<WishlistResponseDto> addToWishlist(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                @RequestBody WishlistRequestDto requestDto) {
        WishlistResponseDto responseDto = wishlistService.addToWishlist(currentUser, requestDto);
        return ApiResponseEntity.success(responseDto);
    }

    @DeleteMapping("/{productId}")
    public ApiResponseEntity<Void> removeFromWishlist(@PathVariable Long productId,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        wishlistService.removeFromWishlist(currentUser, productId);
        return ApiResponseEntity.success(null);
    }

    @DeleteMapping
    public ApiResponseEntity<Void> clearWishlist(@AuthenticationPrincipal CustomUserDetails currentUser) {
        wishlistService.clearWishlist(currentUser);
        return ApiResponseEntity.success(null);
    }

    @GetMapping("/check/{productId}")
    public ApiResponseEntity<Boolean> isProductInWishlist(@PathVariable Long productId,
                                                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        boolean isInWishlist = wishlistService.isProductInWishlist(currentUser, productId);
        return ApiResponseEntity.success(isInWishlist);
    }
}
