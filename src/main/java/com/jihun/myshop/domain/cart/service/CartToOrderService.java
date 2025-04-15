package com.jihun.myshop.domain.cart.service;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto;
import com.jihun.myshop.domain.cart.repository.CartRepository;
import com.jihun.myshop.domain.order.entity.dto.OrderDto;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderCreateDto;
import com.jihun.myshop.domain.order.entity.dto.OrderItemDto;
import com.jihun.myshop.domain.order.entity.dto.OrderItemDto.OrderItemCreateDto;
import com.jihun.myshop.domain.order.entity.mapper.OrderItemMapper;
import com.jihun.myshop.domain.order.service.OrderService;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.exception.ErrorCode;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto.*;
import static com.jihun.myshop.domain.order.entity.dto.OrderDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartToOrderService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;


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
    public OrderResponseDto createOrderFromCart(CustomUserDetails currentUser, CartOrderDto request) {
        User user = getUserByIdWithCart(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CustomException(CART_IS_EMPTY);
        }

        List<OrderItemCreateDto> orderItems = cart.getItems().stream()
                .map(item -> OrderItemCreateDto.builder()
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        OrderCreateDto orderCreateDto = OrderCreateDto.builder()
                .items(orderItems)
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.isSameAsBillingAddress() ? null : request.getBillingAddress())
                .sameAsBillingAddress(request.isSameAsBillingAddress())
                .shippingFee(request.getShippingFee())
                .discountAmount(request.getDiscountAmount())
                .build();

        OrderResponseDto orderResponse = orderService.createOrder(orderCreateDto, currentUser);
        cart.clear();
        cartRepository.save(cart);

        return orderResponse;
    }

    @Transactional
    public OrderResponseDto createOrderFromSelectedCartItems(CustomUserDetails currentUser, CartOrderSelectDto request) {
        return null;
    }
}
