package com.jihun.myshop.domain.order.service;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderItem;
import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.entity.mapper.OrderMapper;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressCreate;
import com.jihun.myshop.domain.user.repository.UserRepository;
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

import java.math.BigDecimal;
import java.util.List;

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


    private Product getProductById(OrderItemCreateDto item) {
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
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }
    private Order findOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }


    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto orderCreateDto, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());

        // 배송 주소
        Address shippingAddress = createAddress(orderCreateDto.getShippingAddress(), user);
        // 결제 주소
        Address billingAddress = orderCreateDto.isSameAsBillingAddress() ?
                shippingAddress : createAddress(orderCreateDto.getBillingAddress(), user);


        Order order = Order.createOrder(
                user,
                shippingAddress,
                billingAddress,
                orderCreateDto.getShippingFee()
        );

        for (OrderItemCreateDto item : orderCreateDto.getItems()) {
            Product product = getProductById(item);

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new CustomException(OUT_OF_STOCK);
            }

            OrderItem orderItem = OrderItem.createOrderItem(product, item.getQuantity());
            order.addOrderItem(orderItem);

            product.updateStockQuantity(product.getStockQuantity() - item.getQuantity());
        }

        if (orderCreateDto.getDiscountAmount() != null && orderCreateDto.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.applyDiscount(orderCreateDto.getDiscountAmount());
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.fromEntity(savedOrder);
    }

    public OrderResponseDto getOrder(Long orderId) {
        Order order = getOrderById(orderId);

        return orderMapper.fromEntity(order);
    }

    public OrderResponseDto getOrderByOrderNumber(String orderNumber) {
        Order order = findOrderByOrderNumber(orderNumber);
        return orderMapper.fromEntity(order);
    }

    public PageResponse<OrderResponseDto> getUserOrders(Long userId, CustomPageRequest pageRequest) {
        User user = getUserById(userId);
        Pageable pageable = pageRequest.toPageRequest();

        Page<Order> orderPage = orderRepository.findByUser(user, pageable);
        Page<OrderResponseDto> responsePage = orderPage.map(orderMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = getOrderById(orderId);
        order.updateOrderStatus(orderStatus);
        return orderMapper.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, OrderCancelDto cancelDto) {
        Order order = getOrderById(orderId);
        order.cancelOrder(cancelDto.getReason());
        return orderMapper.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto setTrackingNumber(Long orderId, String trackingNumber) {
        Order order = getOrderById(orderId);
        order.setTrackingNumber(trackingNumber);
        return orderMapper.fromEntity(order);
    }

    public PageResponse<OrderResponseDto> getOrdersByStatusToUser(List<OrderStatus> statuses, CustomPageRequest pageRequest, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());
        Pageable pageable = pageRequest.toPageRequest();

        Page<Order> orderPage = orderRepository.findByUserAndOrderStatusIn(user, statuses, pageable);
        Page<OrderResponseDto> responsePage = orderPage.map(orderMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    public PageResponse<OrderResponseDto> getOrdersByStatusToAdmin(List<OrderStatus> statuses, CustomPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageRequest();

        Page<Order> orderPage = orderRepository.findByOrderStatusIn(statuses, pageable);
        Page<OrderResponseDto> responsePage = orderPage.map(orderMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByOrderStatus(status);
    }
}
