package com.jihun.myshop.domain.cart.service;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.CartItem;
import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto;
import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto.CartOrderSelectDto;
import com.jihun.myshop.domain.cart.validator.CartToOrderValidator;
import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderItem;
import com.jihun.myshop.domain.order.entity.mapper.OrderMapper;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressCreateDto;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto.CartOrderDto;
import static com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import static com.jihun.myshop.global.exception.ErrorCode.USER_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartToOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartToOrderValidator cartToOrderValidator;

    private final CartService cartService;


    private static void applyDiscount(CartOrderDto request, Order order) {
        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.applyDiscount(request.getDiscountAmount());
        }
    }
    private Address createAddress(AddressCreateDto address, User user) {
        return Address.builder()
                .user(user)
                .recipientName(address.getRecipientName())
                .zipCode(address.getZipCode())
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .phone(address.getPhone())
                .isDefault(address.isDefault())
                .build();
    }
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
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
    public OrderResponseDto createOrderFromCart(CartOrderDto request, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        cartToOrderValidator.validateIsEmptyCart(cart);

        Address shippingAddress = createAddress(request.getShippingAddress(), user);
        Address billingAddress = request.isSameAsBillingAddress() ?
                shippingAddress : createAddress(request.getBillingAddress(), user);

        Order order = Order.createOrder(
                user,
                shippingAddress,
                billingAddress,
                request.getShippingFee()
        );

        for (CartItem cartItem : cart.getItems()) {
            cartToOrderValidator.checkStockQuantity(cartItem.getProduct().getStockQuantity(), cartItem.getQuantity());

            OrderItem orderItem = OrderItem.createOrderItem(cartItem.getProduct(), cartItem.getQuantity());
            order.addOrderItem(orderItem);

            cartItem.getProduct().updateStockQuantity(cartItem.getProduct().getStockQuantity() - cartItem.getQuantity());
        }

        applyDiscount(request, order);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(currentUser);

        return orderMapper.fromEntity(savedOrder);
    }

    @Transactional
    public OrderResponseDto createOrderFromSelectedCartItems(CartOrderSelectDto request, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());
        Cart cart = getOrCreateCart(user);

        cartToOrderValidator.validateCartItemIds(request.getCartItemIds());

        Address shippingAddress = createAddress(request.getShippingAddress(), user);
        Address billingAddress = request.isSameAsBillingAddress() ?
                shippingAddress : createAddress(request.getBillingAddress(), user);

        Order order = Order.createOrder(
                user,
                shippingAddress,
                billingAddress,
                request.getShippingFee()
        );

        for (CartItem cartItem : cart.getItems()) {
            if (request.getCartItemIds().contains(cartItem.getId())) {
                cartToOrderValidator.checkStockQuantity(cartItem.getProduct().getStockQuantity(), cartItem.getQuantity());

                OrderItem orderItem = OrderItem.createOrderItem(cartItem.getProduct(), cartItem.getQuantity());
                order.addOrderItem(orderItem);

                cartItem.getProduct().updateStockQuantity(cartItem.getProduct().getStockQuantity() - cartItem.getQuantity());
            }
        }

        if (request.getDiscountAmount() != null && request.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.applyDiscount(request.getDiscountAmount());
        }

        Order savedOrder = orderRepository.save(order);

        for (Long cartItemId : request.getCartItemIds()) {
            cartService.removeCartItem(currentUser, cartItemId);
        }

        return orderMapper.fromEntity(savedOrder);
    }
}
