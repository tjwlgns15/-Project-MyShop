package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.Review;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto.SummaryDto;
import com.jihun.myshop.domain.product.entity.mapper.ReviewMapper;
import com.jihun.myshop.domain.product.event.ReviewAddedEvent;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.product.repository.ReviewRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.ReviewDto.ReviewRequestDto;
import static com.jihun.myshop.domain.product.entity.dto.ReviewDto.ReviewResponseDto;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    private final ApplicationEventPublisher eventPublisher;


    private Review getReviewById(Long reviewId) {
        return reviewRepository.findByIdWithUserAndProduct(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));
    }
    private Product getProductById(Long productId) {
        return productRepository.findByIdWithCategoryAndSeller(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
    private void validateNoExistingReview(Long userId, Long productId) {
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new CustomException(REVIEW_ALREADY_EXISTS);
        }
    }
    private void validatePurchaseVerification(Long userId, Long productId) {
        List<Order> userOrders = orderRepository.findByUserId(userId);

        boolean isPurchased = userOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(orderItem -> orderItem.getProduct().getId().equals(productId));

        if (!isPurchased) {
            throw new CustomException(REVIEW_NOT_ALLOWED);
        }
    }
    private void validateUserAccess(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
    }


    @Transactional
    public ReviewResponseDto createReview(Long productId, CustomUserDetails currentUser, ReviewRequestDto requestDto) {
        User user = getUserById(currentUser.getId());
        Product product = getProductById(productId);

        // 검증 메서드 호출 - 각 메서드에서 필요 시 예외 발생
        validateNoExistingReview(user.getId(), product.getId());
        validatePurchaseVerification(user.getId(), product.getId());

        Review review = new Review(user, product, requestDto);
        Review savedReview = reviewRepository.save(review);

        eventPublisher.publishEvent(new ReviewAddedEvent(savedReview));

        return reviewMapper.fromEntity(savedReview);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, CustomUserDetails currentUser, ReviewRequestDto requestDto) {
        Review review = getReviewById(reviewId);
        validateUserAccess(review, currentUser.getId());

        review.updateReview(requestDto.getRating(), requestDto.getComment());

        return reviewMapper.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, CustomUserDetails currentUser) {
        Review review = getReviewById(reviewId);
        validateUserAccess(review, currentUser.getId());

        Product product = review.getProduct();
        product.removeReview(review);
        reviewRepository.delete(review);
    }

    public ReviewResponseDto getReview(Long reviewId) {
        Review review = getReviewById(reviewId);

        return reviewMapper.fromEntity(review);
    }

    public CustomPageResponse<ReviewResponseDto> getProductReviews(Long productId, CustomPageRequest pageRequest) {
        Product product = getProductById(productId);
        Pageable pageable = pageRequest.toPageRequest();

        Page<Review> reviewPage = reviewRepository.findByProduct(product, pageable);
        Page<ReviewResponseDto> responsePage = reviewPage.map(reviewMapper::fromEntity);

        return CustomPageResponse.fromPage(responsePage);
    }

    public CustomPageResponse<ReviewResponseDto> getUserReviews(CustomUserDetails currentUser, CustomPageRequest pageRequest) {
        User user = getUserById(currentUser.getId());
        Pageable pageable = pageRequest.toPageRequest();

        Page<Review> reviewPage = reviewRepository.findByUserId(user.getId(), pageable);
        Page<ReviewResponseDto> responsePage = reviewPage.map(reviewMapper::fromEntity);

        return CustomPageResponse.fromPage(responsePage);
    }

    public SummaryDto getProductReviewSummary(Long productId) {
        Product product = getProductById(productId);

        return reviewMapper.toSummaryDto(
                product.getId(),
                product.getTotalReviews(),
                product.getAverageRating()
        );
    }
}