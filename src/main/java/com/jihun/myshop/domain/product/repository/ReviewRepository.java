package com.jihun.myshop.domain.product.repository;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.product WHERE r.id = :id")
    Optional<Review> findByIdWithUserAndProduct(@Param("id") Long id);

    Page<Review> findByProduct(Product product, Pageable pageable);

    Page<Review> findByUserId(Long id, Pageable pageable);
}
