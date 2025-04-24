package com.jihun.myshop.domain.recommendation.service.impl;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.recommendation.entity.ProductRecommendation;
import com.jihun.myshop.domain.recommendation.entity.UserProductInteraction;
import com.jihun.myshop.domain.recommendation.entity.dto.RecommendationResponseDto;
import com.jihun.myshop.domain.recommendation.entity.mapper.RecommendationMapper;
import com.jihun.myshop.domain.recommendation.repository.ProductRecommendationRepository;
import com.jihun.myshop.domain.recommendation.repository.UserProductInteractionRepository;
import com.jihun.myshop.domain.recommendation.service.RecommendationService;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.exception.ErrorCode;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserProductInteractionRepository interactionRepository;
    private final ProductRecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
    }
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
    private List<Product> getPopularProducts(int limit) {
        List<Object[]> popularProductIds = interactionRepository.findMostPopularProducts(limit);
        List<Long> productIds = popularProductIds.stream()
                .map(array -> (Long) array[0])
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return productRepository.findTopByOrderByAverageRatingDesc(limit);
        }

        List<Product> products = productRepository.findAllById(productIds);

        // 원래 순서 유지하기 위한 정렬
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public List<RecommendationResponseDto> getRecommended(CustomUserDetails currentUser, int limit) {

        if (currentUser == null) return getPopular(limit);

        User user = getUserById(currentUser.getId());

        // 추천 목록
        List<ProductRecommendation> recommendations = recommendationRepository.findTopNByUserAndType(
                user,
                ProductRecommendation.RecommendationType.HYBRID,
                limit
        );

        // 결과가 모자라면 인기 상품 추가
        if (recommendations.size() < limit) {
            List<Product> recommendedProducts = recommendations.stream()
                    .map(ProductRecommendation::getProduct)
                    .collect(Collectors.toList());

            List<Product> popularProducts = getPopularProducts(limit);

            // 중복 상품 제거
            popularProducts.removeIf(product ->
                    recommendedProducts.stream().anyMatch(recProduct ->
                            recProduct.getId().equals(product.getId())
                    )
            );

            // 필요한 만큼만 인기 상품 추가
            int remaining = limit - recommendedProducts.size();
            if (remaining > 0 && !popularProducts.isEmpty()) {
                recommendedProducts.addAll(
                        popularProducts.subList(0, Math.min(popularProducts.size(), remaining))
                );
            }

            return recommendedProducts.stream()
                    .map(recommendationMapper::fromEntity)
                    .collect(Collectors.toList());
        }

        return recommendations.stream()
                .map(ProductRecommendation::getProduct)
                .map(recommendationMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponseDto> getSimilar(Long productId, int limit) {
        Product product = getProductById(productId);

        Category category = product.getCategory();
        if (category == null) {
            return getPopularProducts(limit).stream()
                    .map(recommendationMapper::fromEntity)
                    .collect(Collectors.toList());
        }

        List<Product> categoryProducts = productRepository.findByCategoryAndIdNot(category, productId);
        categoryProducts.sort(Comparator.comparing(Product::getAverageRating).reversed());

        return categoryProducts.stream()
                .limit(limit)
                .map(recommendationMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponseDto> getPopular(int limit) {
        List<Product> popularProducts = getPopularProducts(limit);

        return popularProducts.stream()
                .map(recommendationMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponseDto> getRecentlyViewed(CustomUserDetails currentUser, int limit) {
        User user = getUserById(currentUser.getId());

        List<UserProductInteraction> recentlyViewedProducts = interactionRepository.findRecentlyViewedProducts(user, limit);

        return recentlyViewedProducts.stream()
                .map(UserProductInteraction::getProduct)
                .map(recommendationMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void refreshRecommendations() {
        List<User> users = interactionRepository.findAll().stream()
                .map(UserProductInteraction::getUser)
                .distinct()
                .collect(Collectors.toList());

        for (User user : users) {
            // 기존 추천 삭제
            recommendationRepository.deleteByUser(user);
            // 새로운 추천 생성
            generateRecommendationsForUser(user);
        }
    }

    @Transactional
    public void generateRecommendationsForUser(User user) {

        // 협업 기반 추천
        List<Product> collaborativeProducts = generateCollaborativeFilteringRecommendations(user);
        saveRecommendations(user, collaborativeProducts,
                ProductRecommendation.RecommendationType.COLLABORATIVE_FILTERING);

        // 콘텐츠 기반 추천
        List<Product> contentBasedProducts = generateContentBasedRecommendations(user);
        saveRecommendations(user, contentBasedProducts,
                ProductRecommendation.RecommendationType.CONTENT_BASED);

        // 추천 결합
        List<Product> hybridProducts = generateHybridRecommendations(user, collaborativeProducts, contentBasedProducts);
        saveRecommendations(user, hybridProducts,
                ProductRecommendation.RecommendationType.HYBRID);
    }

    private List<Product> generateCollaborativeFilteringRecommendations(User user) {
        // 사용자의 상호작용 데이터
        List<UserProductInteraction> userInteractions = interactionRepository.findByUser(user);

        // 사용자가 상호작용한 제품 ID 목록
        Set<Long> interactedProductIds = userInteractions.stream()
                .map(interaction -> interaction.getProduct().getId())
                .collect(Collectors.toSet());

        // 사용자 기반 협업 필터링 구현
        // 1. 현재 사용자의 상품 점수 프로필 생성
        Map<Long, Double> userProductScores = userInteractions.stream()
                .collect(Collectors.toMap(
                        interaction -> interaction.getProduct().getId(),
                        UserProductInteraction::calculateInterestScore
                ));

        // 2. 다른 모든 사용자와의 유사도 계산
        List<UserProductInteraction> allInteractions = interactionRepository.findAll();
        Map<User, Double> userSimilarities = new HashMap<>();

        // 다른 모든 사용자 목록
        Set<User> otherUsers = allInteractions.stream()
                .map(UserProductInteraction::getUser)
                .filter(u -> !u.getId().equals(user.getId()))
                .collect(Collectors.toSet());

        for (User otherUser : otherUsers) {
            List<UserProductInteraction> otherUserInteractions = allInteractions.stream()
                    .filter(i -> i.getUser().getId().equals(otherUser.getId()))
                    .collect(Collectors.toList());

            Map<Long, Double> otherUserProductScores = otherUserInteractions.stream()
                    .collect(Collectors.toMap(
                            interaction -> interaction.getProduct().getId(),
                            UserProductInteraction::calculateInterestScore
                    ));

            // 피어슨 상관계수로 유사도 계산 (간소화 버전)
            double similarity = calculateUserSimilarity(userProductScores, otherUserProductScores);
            userSimilarities.put(otherUser, similarity);
        }

        // 3. 가장 유사한 사용자 N명 선택
        List<User> similarUsers = userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<User, Double>comparingByValue().reversed())
                .limit(10) // 상위 10명의 유사 사용자 선택
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 4. 유사 사용자가 상호작용했지만 현재 사용자는 상호작용하지 않은 제품 찾기
        Map<Long, Double> candidateProducts = new HashMap<>();

        for (User similarUser : similarUsers) {
            double similarity = userSimilarities.get(similarUser);

            // 유사도가 양수인 경우에만 고려
            if (similarity > 0) {
                List<UserProductInteraction> similarUserInteractions =
                        allInteractions.stream()
                                .filter(i -> i.getUser().getId().equals(similarUser.getId()))
                                .collect(Collectors.toList());

                for (UserProductInteraction interaction : similarUserInteractions) {
                    Long productId = interaction.getProduct().getId();

                    // 현재 사용자가 상호작용하지 않은 제품만 고려
                    if (!interactedProductIds.contains(productId)) {
                        double score = interaction.calculateInterestScore() * similarity;
                        candidateProducts.merge(productId, score, Double::sum);
                    }
                }
            }
        }

        // 5. 점수가 높은 상품 추천
        List<Long> recommendedProductIds = candidateProducts.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(20) // 상위 20개 상품 추천
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 추천된 제품 정보 조회
        return productRepository.findAllById(recommendedProductIds);
    }

    private double calculateUserSimilarity(Map<Long, Double> user1Scores, Map<Long, Double> user2Scores) {
        // 두 사용자가 공통으로 평가한 제품 목록
        Set<Long> commonProducts = new HashSet<>(user1Scores.keySet());
        commonProducts.retainAll(user2Scores.keySet());

        if (commonProducts.isEmpty()) {
            return 0.0; // 공통 제품이 없으면 유사도 0
        }

        double sumUser1 = 0, sumUser2 = 0, sumProduct = 0;
        double sumSqUser1 = 0, sumSqUser2 = 0;

        for (Long productId : commonProducts) {
            double score1 = user1Scores.get(productId);
            double score2 = user2Scores.get(productId);

            sumUser1 += score1;
            sumUser2 += score2;
            sumProduct += score1 * score2;
            sumSqUser1 += score1 * score1;
            sumSqUser2 += score2 * score2;
        }

        double size = commonProducts.size();
        double numerator = sumProduct - (sumUser1 * sumUser2 / size);
        double denominator = Math.sqrt((sumSqUser1 - (sumUser1 * sumUser1) / size) *
                (sumSqUser2 - (sumUser2 * sumUser2) / size));

        if (denominator == 0) {
            return 0.0;
        }

        return numerator / denominator;
    }

    private List<Product> generateContentBasedRecommendations(User user) {
        // 사용자의 상호작용 데이터
        List<UserProductInteraction> userInteractions = interactionRepository.findByUser(user);

        // 상호작용 점수가 높은 상위 제품 선택
        List<Product> userFavoriteProducts = userInteractions.stream()
                .sorted(Comparator.comparing(UserProductInteraction::calculateInterestScore).reversed())
                .limit(10) // 사용자가 가장 관심을 보인 10개 제품
                .map(UserProductInteraction::getProduct)
                .collect(Collectors.toList());

        // 사용자가 이미 상호작용한 제품 ID 목록
        Set<Long> interactedProductIds = userInteractions.stream()
                .map(interaction -> interaction.getProduct().getId())
                .collect(Collectors.toSet());

        // 선호하는 카테고리 파악
        Map<Category, Integer> categoryPreferences = new HashMap<>();

        for (Product product : userFavoriteProducts) {
            Category category = product.getCategory();
            if (category != null) {
                categoryPreferences.merge(category, 1, Integer::sum);
            }
        }

        // 카테고리별 선호도 순으로 정렬
        List<Category> preferredCategories = categoryPreferences.entrySet().stream()
                .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 각 선호 카테고리에서 제품 추천
        List<Product> recommendedProducts = new ArrayList<>();

        for (Category category : preferredCategories) {
            List<Product> categoryProducts = productRepository.findByCategory(category);

            // 사용자가 상호작용하지 않은 제품만 필터링
            List<Product> newProducts = categoryProducts.stream()
                    .filter(p -> !interactedProductIds.contains(p.getId()))
                    .collect(Collectors.toList());

            // 제품의 평균 평점에 따라 정렬
            newProducts.sort(Comparator.comparing(Product::getAverageRating).reversed());

            // 각 카테고리에서 최대 5개 제품 추천
            recommendedProducts.addAll(newProducts.stream()
                    .limit(5)
                    .collect(Collectors.toList()));

            // 총 20개 제품에 도달하면 중단
            if (recommendedProducts.size() >= 20) {
                break;
            }
        }

        // 필요한 경우 다른 인기 제품으로 보충
        if (recommendedProducts.size() < 20) {
            List<Product> popularProducts = getPopularProducts(20);

            // 이미 추천 목록에 있거나 사용자가 상호작용한 제품 제외
            Set<Long> existingRecommendedIds = recommendedProducts.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());

            List<Product> additionalProducts = popularProducts.stream()
                    .filter(p -> !existingRecommendedIds.contains(p.getId()) &&
                            !interactedProductIds.contains(p.getId()))
                    .limit(20 - recommendedProducts.size())
                    .collect(Collectors.toList());

            recommendedProducts.addAll(additionalProducts);
        }

        return recommendedProducts;
    }

    private List<Product> generateHybridRecommendations(User user, List<Product> collaborativeProducts, List<Product> contentBasedProducts) {

        // 두 방식의 추천 결과 결합
        Map<Long, Double> productScores = new HashMap<>();

        // 협업 필터링 결과에 가중치 부여 (60%)
        double cfWeight = 0.6;
        for (int i = 0; i < collaborativeProducts.size(); i++) {
            Product product = collaborativeProducts.get(i);
            double score = (collaborativeProducts.size() - i) * cfWeight;
            productScores.put(product.getId(), score);
        }

        // 콘텐츠 기반 결과에 가중치 부여 (40%)
        double cbWeight = 0.4;
        for (int i = 0; i < contentBasedProducts.size(); i++) {
            Product product = contentBasedProducts.get(i);
            double score = (contentBasedProducts.size() - i) * cbWeight;
            productScores.merge(product.getId(), score, Double::sum);
        }

        // 점수가 높은 순으로 상품 정렬
        List<Long> hybridProductIds = productScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 정렬된 순서대로 제품 목록 조회
        List<Product> hybridProducts = new ArrayList<>();
        for (Long productId : hybridProductIds) {
            productRepository.findById(productId).ifPresent(hybridProducts::add);
        }

        return hybridProducts;
    }

    private void saveRecommendations(User user, List<Product> products, ProductRecommendation.RecommendationType type) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            double score = 1.0 - ((double) i / products.size()); // 점수 정규화 (0~1 사이)

            ProductRecommendation recommendation = ProductRecommendation.builder()
                    .user(user)
                    .product(product)
                    .score(score)
                    .recommendationType(type)
                    .build();

            recommendationRepository.save(recommendation);
        }
    }
}
