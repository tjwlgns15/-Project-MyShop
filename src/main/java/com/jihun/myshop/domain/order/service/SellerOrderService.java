package com.jihun.myshop.domain.order.service;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.entity.mapper.OrderMapper;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.exception.ErrorCode;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import com.jihun.myshop.global.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final AuthorizationService authorizationService;


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
    // 판매자로서의 주문 접근 권한 검사
    private void validateSellerOrderAccess(CustomUserDetails currentUser, Product product) {
        // 관리자는 모든 주문에 접근 가능
        if (authorizationService.isAdmin(currentUser)) {
            return;
        }

        // 판매자 권한 확인
        if (!authorizationService.isSeller(currentUser)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }

        // 본인이 판매한 상품의 주문인지 확인
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
    }

    public PageResponse<OrderResponseDto> getOrdersByProduct(Long productId, CustomPageRequest pageRequest, CustomUserDetails currentUser) {
        Product product = getProductById(productId);

        validateSellerOrderAccess(currentUser, product);

        Pageable pageable = pageRequest.toPageRequest();
        Page<Order> orderPage = orderRepository.findByProductId(productId, pageable);
        Page<OrderResponseDto> responsePage = orderPage.map(orderMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    public PageResponse<OrderResponseDto> getOrdersByProductAndStatus(Long productId, List<OrderStatus> statuses, CustomPageRequest pageRequest, CustomUserDetails currentUser) {
        Product product = getProductById(productId);

        validateSellerOrderAccess(currentUser, product);

        Pageable pageable = pageRequest.toPageRequest();
        Page<Order> orderPage = orderRepository.findByProductIdAndOrderStatusIn(productId, statuses, pageable);
        Page<OrderResponseDto> responsePage = orderPage.map(orderMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }


}

