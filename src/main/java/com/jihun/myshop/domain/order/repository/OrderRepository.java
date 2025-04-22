package com.jihun.myshop.domain.order.repository;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByUserAndOrderStatusIn(User user, List<OrderStatus> orderStatuses, Pageable pageable);
    Page<Order> findByOrderStatusIn(List<OrderStatus> orderStatuses, Pageable pageable);

    long countByOrderStatus(OrderStatus status);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    Page<Order> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId AND o.orderStatus IN :statuses")
    Page<Order> findByProductIdAndOrderStatusIn(@Param("productId") Long productId, @Param("statuses") List<OrderStatus> statuses, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.product.seller.id = :sellerId")
    Page<Order> findBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    List<Order> findByUserId(Long userId);
}
