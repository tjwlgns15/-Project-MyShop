package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.domain.user.entity.dto.UserResponse;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.ProductDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;

    @PostMapping("new")
    public ApiResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreate request,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductResponse response = productService.createProduct(request, userDetails);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/{productId}")
    public ApiResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ApiResponseEntity.success(response);
    }

    @GetMapping
    public ApiResponseEntity<PageResponse<ProductResponse>> getProducts(CustomPageRequest pageRequest) {
        PageResponse<ProductResponse> responses = productService.getProducts(pageRequest);
        return ApiResponseEntity.success(responses);
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponseEntity<PageResponse<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false, defaultValue = "false") boolean includeSubcategories,
            CustomPageRequest pageRequest) {

        PageResponse<ProductResponse> responses;
        if (includeSubcategories) {
            responses = productService.getProductsByCategoryIncludingSubcategories(categoryId, pageRequest);
        } else {
            responses = productService.getProductsByCategory(categoryId, pageRequest);
        }

        return ApiResponseEntity.success(responses);
    }

    @PutMapping("/{productId}")
    public ApiResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdate request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 사용자 권한은 서비스 레이어에서 검증
        ProductResponse response = productService.updateProduct(productId, request, userDetails);
        return ApiResponseEntity.success(response);
    }

    @DeleteMapping("/{productId}")
    public ApiResponseEntity<ProductResponse> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 사용자 권한은 서비스 레이어에서 검증
        ProductResponse response = productService.deleteProduct(productId, userDetails);
        return ApiResponseEntity.success(response);
    }






}
