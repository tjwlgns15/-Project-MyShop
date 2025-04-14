package com.jihun.myshop.domain.order.repository;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.Payment;
import com.jihun.myshop.domain.order.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByPaymentKey(String paymentKey);

    Page<Payment> findByPaymentStatusIn(Collection<PaymentStatus> paymentStatuses, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId")
    Page<Payment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    long countByPaymentStatus(PaymentStatus paymentStatus);
}
