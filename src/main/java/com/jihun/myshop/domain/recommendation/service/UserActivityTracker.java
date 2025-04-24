package com.jihun.myshop.domain.recommendation.service;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.Review;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.recommendation.entity.UserProductInteraction;
import com.jihun.myshop.domain.recommendation.repository.UserProductInteractionRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.exception.ErrorCode;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserActivityTracker {

    private final UserProductInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
    }
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }


    @Transactional
    public void trackProductView(CustomUserDetails currentUser, Long productId) {
        if (currentUser == null) return;

        User user = getUserById(currentUser.getId());
        Product product = getProductById(productId);

        UserProductInteraction interaction = interactionRepository.findByUserAndProduct(user, product)
                .orElse(new UserProductInteraction(user, product));

        interaction.incrementViewCount();
        interaction.setLastViewedAt(LocalDateTime.now());
        interactionRepository.save(interaction);
    }

    @Transactional
    public void trackProductAddedToCart(Cart cart, Product product) {
        if (cart == null || cart.getUser() == null) return;

        User user = cart.getUser();
        UserProductInteraction interaction = interactionRepository
                .findByUserAndProduct(user, product)
                .orElse(new UserProductInteraction(user, product));

        interaction.incrementCartCount();
        interactionRepository.save(interaction);
    }

    @Transactional
    public void trackProductPurchase(User user, Product product, int quantity) {
        if (user == null) return;

        UserProductInteraction interaction = interactionRepository
                .findByUserAndProduct(user, product)
                .orElse(new UserProductInteraction(user, product));

        interaction.incrementPurchaseCount(quantity);
        interactionRepository.save(interaction);
    }

    @Transactional
    public void trackProductAddedToWishlist(User user, Product product) {
        if (user == null) return;

        UserProductInteraction interaction = interactionRepository
                .findByUserAndProduct(user, product)
                .orElse(new UserProductInteraction(user, product));

        interaction.setAddedToWishlist(true);
        interactionRepository.save(interaction);
    }

    @Transactional
    public void trackProductReviewed(Review review) {
        if (review.getUser() == null || review.getProduct() == null) return;

        User user = review.getUser();
        Product product = review.getProduct();

        UserProductInteraction interaction = interactionRepository
                .findByUserAndProduct(user, product)
                .orElse(new UserProductInteraction(user, product));

        interaction.setReviewRating(review.getRating());
        interactionRepository.save(interaction);
    }



}
