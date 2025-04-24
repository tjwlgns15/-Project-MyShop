package com.jihun.myshop.domain.cart.service;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.CartItem;
import com.jihun.myshop.domain.cart.entity.dto.CartDto.CartResponseDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemCreateDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemUpdateDto;
import com.jihun.myshop.domain.cart.entity.mapper.CartMapper;
import com.jihun.myshop.domain.cart.event.CartItemAddedEvent;
import com.jihun.myshop.domain.cart.repository.CartRepository;
import com.jihun.myshop.domain.cart.validator.CartValidator;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartValidator cartValidator;
    private final CartMapper cartMapper;

    private final ApplicationEventPublisher eventPublisher;


    private User getUserByIdWithCart(Long id) {
        return userRepository.findByIdWithCart(id)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
    private void checkProductStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new CustomException(OUT_OF_STOCK);
        }
    }
    private Cart getOrCreateCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .build();
        }
        return cart;
    }


    @Transactional
    public CartResponseDto addItemToCart(CustomUserDetails currentUser, CartItemCreateDto requestDto) {
        cartValidator.validateAddCartItemRequest(requestDto);

        User user = getUserByIdWithCart(currentUser.getId());
        Product product = getProductById(requestDto.getProductId());
        Cart cart = getOrCreateCart(user);

        cart.addItem(product, requestDto.getQuantity());
        cartRepository.save(cart);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(CART_ITEM_NOT_FOUND));

        eventPublisher.publishEvent(new CartItemAddedEvent(cart, cartItem));

        return cartMapper.fromEntity(cart);
    }


    public CartResponseDto getCart(CustomUserDetails currentUser) {
        User user = getUserByIdWithCart(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        return cartMapper.fromEntity(cart);
    }

    @Transactional
    public CartResponseDto updateCartItem(CustomUserDetails currentUser, CartItemUpdateDto requestDto) {
        cartValidator.validateUpdateCartItemRequest(requestDto);

        User user = getUserByIdWithCart(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        cart.updateQuantity(requestDto.getCartItemId(), requestDto.getQuantity());
        cartRepository.save(cart);

        return cartMapper.fromEntity(cart);
    }

    @Transactional
    public CartResponseDto removeCartItem(CustomUserDetails currentUser, Long cartItemId) {
        User user = getUserByIdWithCart(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        cart.removeItem(cartItemId);
        cartRepository.save(cart);

        return cartMapper.fromEntity(cart);
    }

    @Transactional
    public CartResponseDto clearCart(CustomUserDetails currentUser) {
        User user = getUserByIdWithCart(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        cart.clear();
        cartRepository.save(cart);

        return cartMapper.fromEntity(cart);
    }
}
