package com.jihun.myshop.domain.recommendation.service;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.recommendation.entity.dto.RecommendationResponseDto;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;

import java.util.List;

public interface RecommendationService {

    // 사용자에게 추천 상품 목록 제공
    List<RecommendationResponseDto> getRecommended(CustomUserDetails currentUser, int limit);

    // 특정 상품과 유사한 상품 목록 제공
    List<RecommendationResponseDto> getSimilar(Long productId, int limit);

    // 인기 상품 목록 제공
    List<RecommendationResponseDto> getPopular(int limit);

    // 최근 본 상품 목록 제공
    List<RecommendationResponseDto> getRecentlyViewed(CustomUserDetails currentUser, int limit);

    // 추천 시스템 갱신/재계산
    void refreshRecommendations();
}
