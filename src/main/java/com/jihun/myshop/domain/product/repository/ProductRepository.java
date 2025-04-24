package com.jihun.myshop.domain.product.repository;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
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
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 페이징 메서드
    Page<Product> findByProductStatusIn(List<ProductStatus> statuses, Pageable pageable);
    Page<Product> findByCategoryIdAndProductStatusIn(Long categoryId, List<ProductStatus> statuses, Pageable pageable);
    Page<Product> findByCategoryIdInAndProductStatusIn(List<Long> categoryIds, List<ProductStatus> statuses, Pageable pageable);

    // 단일 상품 조회 시 페치 조인
    @Query("SELECT p FROM Product p JOIN FETCH p.category JOIN FETCH p.seller WHERE p.id = :productId")
    Optional<Product> findByIdWithCategoryAndSeller(@Param("productId") Long productId);

    // 판매자 상품 조회
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
    Page<Product> findBySellerIdAndProductStatusIn(Long sellerId, List<ProductStatus> statuses, Pageable pageable);

    // 추천
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryAndIdNot(Category category, Long excludeProductId);
    @Query("SELECT p FROM Product p ORDER BY p.averageRating DESC LIMIT :limit")
    List<Product> findTopByOrderByAverageRatingDesc(@Param("limit") int limit);

    Optional<Product> findByName(String name);
}
