package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.entity.dto.ProductDto;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto.ReviewRequestDto;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto.ReviewResponseDto;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto.SummaryDto;
import com.jihun.myshop.domain.product.service.ReviewService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewRestController {

    private final ReviewService reviewService;


    @PostMapping("/products/{productId}")
    public ApiResponseEntity<ReviewResponseDto> createReview(@PathVariable Long productId,
                                                             @AuthenticationPrincipal CustomUserDetails currentUser,
                                                             @Valid @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto responseDto = reviewService.createReview(productId, currentUser, requestDto);
        return ApiResponseEntity.success(responseDto);
    }

    @PutMapping("/{reviewId}")
    public ApiResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId,
                                                             @AuthenticationPrincipal CustomUserDetails currentUser,
                                                             @Valid @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto responseDto = reviewService.updateReview(reviewId, currentUser, requestDto);
        return ApiResponseEntity.success(responseDto);
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                                @AuthenticationPrincipal CustomUserDetails currentUser) {
        reviewService.deleteReview(reviewId, currentUser);
        return ApiResponseEntity.success(null);
    }

    @GetMapping("/{reviewId}")
    public ApiResponseEntity<ReviewResponseDto> getReviewDetail(@PathVariable Long reviewId) {
        ReviewResponseDto responseDto = reviewService.getReview(reviewId);
        return ApiResponseEntity.success(responseDto);
    }

    @GetMapping("/products/{productId}")
    public ApiResponseEntity<PageResponse<ReviewResponseDto>> getProductReviews(@PathVariable Long productId,
                                                                                CustomPageRequest pageRequest) {
        PageResponse<ReviewResponseDto> responseDto = reviewService.getProductReviews(productId, pageRequest);
        return ApiResponseEntity.success(responseDto);
    }

    @GetMapping("/me")
    public ApiResponseEntity<PageResponse<ReviewResponseDto>> getMyReviews(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                           CustomPageRequest pageRequest) {
        PageResponse<ReviewResponseDto> responseDto = reviewService.getUserReviews(currentUser, pageRequest);
        return ApiResponseEntity.success(responseDto);
    }

    @GetMapping("/products/{productId}/summary")
    public ApiResponseEntity<SummaryDto> getProductReviewSummary(@PathVariable Long productId) {
        SummaryDto responseDto = reviewService.getProductReviewSummary(productId);
        return ApiResponseEntity.success(responseDto);
    }
}
