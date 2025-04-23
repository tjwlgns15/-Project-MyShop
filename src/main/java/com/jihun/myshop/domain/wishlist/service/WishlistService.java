package com.jihun.myshop.domain.wishlist.service;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.domain.wishlist.entity.WishlistItem;
import com.jihun.myshop.domain.wishlist.entity.mapper.WishlistMapper;
import com.jihun.myshop.domain.wishlist.repository.WishlistRepository;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.myshop.domain.wishlist.entity.dto.WishlistItemDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }

    private WishlistItem getWishlistItemByUserAndProduct(User user, Product product) {
        return wishlistRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));
    }

    private void validateProductNotInWishlist(User user, Product product) {
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new CustomException(ALREADY_EXIST_ITEM);
        }
    }


    public PageResponse<WishlistResponseDto> getWishlistItems(CustomUserDetails currentUser, CustomPageRequest pageRequest) {
        User user = getUserById(currentUser.getId());
        Pageable pageable = pageRequest.toPageRequest();

        Page<WishlistItem> wishlistItemPage = wishlistRepository.findByUser(user, pageable);
        Page<WishlistResponseDto> responsePage = wishlistItemPage.map(wishlistMapper::fromEntity);

        return PageResponse.fromPage(responsePage);
    }

    @Transactional
    public WishlistResponseDto addToWishlist(CustomUserDetails currentUser, WishlistRequestDto requestDto) {
        User user = getUserById(currentUser.getId());
        Product product = getProductById(requestDto.getProductId());

        validateProductNotInWishlist(user, product);

        WishlistItem wishlistItem = new WishlistItem(user, product);
        WishlistItem savedWishlistItem = wishlistRepository.save(wishlistItem);

        return wishlistMapper.fromEntity(savedWishlistItem);
    }

    @Transactional
    public void removeFromWishlist(CustomUserDetails currentUser, Long productId) {
        User user = getUserById(currentUser.getId());
        Product product = getProductById(productId);
        WishlistItem wishlistItem = getWishlistItemByUserAndProduct(user, product);

        wishlistRepository.delete(wishlistItem);
    }

    @Transactional
    public void clearWishlist(CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());
        List<WishlistItem> wishlistItems = wishlistRepository.findByUser(user);

        wishlistRepository.deleteAll(wishlistItems);
    }

    public boolean isProductInWishlist(CustomUserDetails currentUser, Long productId) {
        User user = getUserById(currentUser.getId());
        Product product = getProductById(productId);

        return wishlistRepository.existsByUserAndProduct(user, product);
    }
}
