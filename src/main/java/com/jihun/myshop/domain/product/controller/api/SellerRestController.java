package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.entity.dto.OrderDto;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import com.jihun.myshop.domain.order.service.SellerOrderService;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.entity.dto.ProductDto;
import com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductResponseDto;
import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/products")
public class SellerRestController {

    private final ProductService productService;
    private final SellerOrderService sellerOrderService;


    @GetMapping
    public ApiResponseEntity<PageResponse<ProductResponseDto>> getSellerProducts(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                 CustomPageRequest pageRequest) {
        PageResponse<ProductResponseDto> response = productService.getSellerProducts(currentUser, pageRequest);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/status/{statuses}")
    public ApiResponseEntity<PageResponse<ProductResponseDto>> getSellerProductsByStatus(@PathVariable List<ProductStatus> statuses,
                                                                                         @AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                         CustomPageRequest pageRequest) {
        PageResponse<ProductResponseDto> response = productService.getSellerProductsByStatus(currentUser, statuses, pageRequest);
        return ApiResponseEntity.success(response);
    }

    // 특정 상품의 모든 주문 조회
    @GetMapping("/{productId}")
    public ApiResponseEntity<PageResponse<OrderResponseDto>> getOrdersByProduct(@PathVariable Long productId,
                                                                                @AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                CustomPageRequest pageRequest) {
        PageResponse<OrderResponseDto> response = sellerOrderService.getOrdersByProduct(productId, pageRequest, currentUser);
        return ApiResponseEntity.success(response);
    }

    // 특정 상품의 특정 상태 주문 조회
    @GetMapping("/{productId}/status/{statuses}")
    public ApiResponseEntity<PageResponse<OrderResponseDto>> getOrdersByProductAndStatus(@PathVariable Long productId,
                                                                                         @PathVariable List<OrderStatus> statuses,
                                                                                         @AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                         CustomPageRequest pageRequest) {
        PageResponse<OrderResponseDto> response = sellerOrderService.getOrdersByProductAndStatus(productId, statuses, pageRequest, currentUser);
        return ApiResponseEntity.success(response);
    }

}
