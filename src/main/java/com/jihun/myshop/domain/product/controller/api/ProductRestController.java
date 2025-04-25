package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.entity.DiscountType;
import com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto;
import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;

    @PostMapping(value = "new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseEntity<ProductWithImageDto.ProductResponseDto> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("discountType") DiscountType discountType,
            @RequestParam("discountValue") BigDecimal discountValue,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // ProductCreateWithImagesDto 객체 생성
        ProductCreateWithImagesDto productCreateDto = ProductCreateWithImagesDto.builder()
                .name(name)
                .description(description)
                .categoryId(categoryId)
                .price(price)
                .stockQuantity(stockQuantity)
                .discountType(discountType)
                .discountValue(discountValue)
                .build();

        ProductResponseDto response = productService.createProductWithImages(
                productCreateDto, mainImage, additionalImages, userDetails);
        return ApiResponseEntity.success(response);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestPart("product") ProductUpdateWithImagesDto productUpdateDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProductResponseDto response = productService.updateProductWithImages(
                productId, productUpdateDto, mainImage, additionalImages, userDetails);
        return ApiResponseEntity.success(response);
    }

    @DeleteMapping("/{productId}")
    public ApiResponseEntity<ProductResponseDto> deleteProduct(
            @PathVariable Long productId,
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
    public ApiResponseEntity<ProductResponseDto> getProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ProductResponseDto response = productService.getProduct(productId, currentUser);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponseEntity<CustomPageResponse<ProductResponseDto>> getProductsByCategory(
            @PathVariable Long categoryId,
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
    public ApiResponseEntity<List<ProductResponseDto>> getRecentlyViewedProducts(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProductResponseDto> responseDto = productService.getRecentlyViewedProducts(currentUser, limit);
        return ApiResponseEntity.success(responseDto);
    }
}