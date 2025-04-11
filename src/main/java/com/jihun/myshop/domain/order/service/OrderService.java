package com.jihun.myshop.domain.order.service;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderItem;
import com.jihun.myshop.domain.order.entity.dto.OrderDto;
import com.jihun.myshop.domain.order.entity.dto.OrderItemDto;
import com.jihun.myshop.domain.order.entity.mapper.OrderMapper;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.dto.AddressDto;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressCreate;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.jihun.myshop.domain.order.entity.dto.OrderDto.*;
import static com.jihun.myshop.domain.order.entity.dto.OrderItemDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;


    @Transactional
    public OrderResponse createOrder(OrderCreate orderCreate, CustomUserDetails currentUser) {
        User user = getUserById(currentUser);

        Address shippingAddress = createAddress(orderCreate.getShippingAddress(), user);
        // 결제 주소
        Address billingAddress = orderCreate.isSameAsBillingAddress() ?
                shippingAddress : createAddress(orderCreate.getBillingAddress(), user);


        Order order = Order.createOrder(
                user,
                shippingAddress,
                billingAddress,
                orderCreate.getShippingFee()
        );

        for (OrderItemCreate item : orderCreate.getItems()) {
            Product product = getProductById(item);

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new CustomException(OUT_OF_STOCK);
            }

            OrderItem orderItem = OrderItem.createOrderItem(product, item.getQuantity());
            order.addOrderItem(orderItem);

            product.updateStockQuantity(product.getStockQuantity() - item.getQuantity());
        }

        if (orderCreate.getDiscountAmount() != null && orderCreate.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.applyDiscount(orderCreate.getDiscountAmount());
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.fromEntity(savedOrder);
    }


    private Product getProductById(OrderItemCreate item) {
        return productRepository.findById(item.getProductId())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
    private Address createAddress(AddressCreate shippingAddress, User user) {
        return Address.builder()
                .user(user)
                .recipientName(shippingAddress.getRecipientName())
                .zipCode(shippingAddress.getZipCode())
                .address1(shippingAddress.getAddress1())
                .address2(shippingAddress.getAddress2())
                .phone(shippingAddress.getPhone())
                .isDefault(shippingAddress.isDefault())
                .build();
    }
    private User getUserById(CustomUserDetails currentUser) {
        return userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
}
