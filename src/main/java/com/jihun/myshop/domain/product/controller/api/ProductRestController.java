package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.jihun.myshop.domain.product.entity.dto.ProductDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;

    @PostMapping("new")
    public ApiResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateDto productCreateDto,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductResponseDto response = productService.createProduct(productCreateDto, userDetails);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/{productId}")
    public ApiResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        ProductResponseDto response = productService.getProduct(productId);
        return ApiResponseEntity.success(response);
    }

    @GetMapping
    public ApiResponseEntity<PageResponse<ProductResponseDto>> getProducts(CustomPageRequest pageRequest) {
        PageResponse<ProductResponseDto> responses = productService.getProducts(pageRequest);
        return ApiResponseEntity.success(responses);
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponseEntity<PageResponse<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId,
                                                                                     @RequestParam(required = false, defaultValue = "false") boolean includeSubcategories,
                                                                                     CustomPageRequest pageRequest) {
        PageResponse<ProductResponseDto> responses;
        if (includeSubcategories) {
            responses = productService.getProductsByCategoryIncludingSubcategories(categoryId, pageRequest);
        } else {
            responses = productService.getProductsByCategory(categoryId, pageRequest);
        }

        return ApiResponseEntity.success(responses);
    }

    @PutMapping("/{productId}")
    public ApiResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productId,
                                                               @RequestBody ProductUpdateDto productUpdateDto,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductResponseDto response = productService.updateProduct(productId, productUpdateDto, userDetails);
        return ApiResponseEntity.success(response);
    }

    @DeleteMapping("/{productId}")
    public ApiResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long productId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductResponseDto response = productService.deleteProduct(productId, userDetails);
        return ApiResponseEntity.success(response);
    }






}
