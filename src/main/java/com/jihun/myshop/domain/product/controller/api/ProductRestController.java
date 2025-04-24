package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.domain.recommendation.entity.UserProductInteraction;
import com.jihun.myshop.domain.recommendation.entity.dto.RecommendationResponseDto;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ApiResponseEntity<CustomPageResponse<ProductResponseDto>> getProducts(CustomPageRequest pageRequest) {
        CustomPageResponse<ProductResponseDto> responses = productService.getProducts(pageRequest);
        return ApiResponseEntity.success(responses);
    }

    @GetMapping("/{productId}")
    public ApiResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId,
                                                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        ProductResponseDto response = productService.getProduct(productId, currentUser);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponseEntity<CustomPageResponse<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId,
                                                                                           @RequestParam(required = false, defaultValue = "false") boolean includeSubcategories,
                                                                                           CustomPageRequest pageRequest) {
        CustomPageResponse<ProductResponseDto> responses;
        if (includeSubcategories) {
            responses = productService.getProductsByCategoryIncludingSubcategories(categoryId, pageRequest);
        } else {
            responses = productService.getProductsByCategory(categoryId, pageRequest);
        }

        return ApiResponseEntity.success(responses);
    }

    @GetMapping("/recent-viewed")
    public ApiResponseEntity<List<ProductResponseDto>> getRecentlyViewedProducts(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                 @RequestParam(defaultValue = "10") int limit) {
        List<ProductResponseDto> responseDto = productService.getRecentlyViewedProducts(currentUser, limit);
        return ApiResponseEntity.success(responseDto);
    }
}
