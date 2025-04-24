package com.jihun.myshop.domain.recommendation.controller.api;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.recommendation.entity.dto.RecommendationResponseDto;
import com.jihun.myshop.domain.recommendation.service.RecommendationService;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserActivityTracker userActivityTracker;

    @GetMapping
    public ApiResponseEntity<List<RecommendationResponseDto>> getRecommendedProducts(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                     @RequestParam(defaultValue = "10") int limit) {
        List<RecommendationResponseDto> responseDto = recommendationService.getRecommended(currentUser, limit);
        return ApiResponseEntity.success(responseDto);
    }

    @GetMapping("/similar/{productId}")
    public ApiResponseEntity<List<RecommendationResponseDto>> getSimilarProducts(@PathVariable Long productId,
                                                                                 @RequestParam(defaultValue = "5") int limit) {
        List<RecommendationResponseDto> responseDto = recommendationService.getSimilar(productId, limit);
        return ApiResponseEntity.success(responseDto);
    }

    @GetMapping("/popular")
    public ApiResponseEntity<List<RecommendationResponseDto>> getPopularProducts(@RequestParam(defaultValue = "10") int limit) {
        List<RecommendationResponseDto> responseDto = recommendationService.getPopular(limit);
        return ApiResponseEntity.success(responseDto);
    }

    @GetMapping("/recently-viewed")
    public ApiResponseEntity<List<RecommendationResponseDto>> getRecentlyViewedProducts(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                                                        @RequestParam(defaultValue = "10") int limit) {
        List<RecommendationResponseDto> responseDto = recommendationService.getRecentlyViewed(currentUser, limit);
        return ApiResponseEntity.success(responseDto);
    }

    @PostMapping("/track-view/{productId}")
        public ApiResponseEntity<Void> trackProductView(@AuthenticationPrincipal CustomUserDetails currentUser,
                                                        @PathVariable Long productId) {
        userActivityTracker.trackProductView(currentUser, productId);
        return ApiResponseEntity.success(null);
    }
}