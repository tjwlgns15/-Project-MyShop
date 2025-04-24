package com.jihun.myshop.domain.recommendation.repository;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.recommendation.entity.UserProductInteraction;
import com.jihun.myshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductInteractionRepository extends JpaRepository<UserProductInteraction, Long> {

    Optional<UserProductInteraction> findByUserAndProduct(User user, Product product);

    List<UserProductInteraction> findByUser(User user);

    @Query("SELECT upi FROM UserProductInteraction upi WHERE upi.user = :user ORDER BY upi.lastViewedAt DESC LIMIT :limit")
    List<UserProductInteraction> findRecentlyViewedProducts(@Param("user") User user, @Param("limit") int limit);
    @Query("SELECT upi FROM UserProductInteraction upi WHERE upi.user = :user AND upi.lastViewedAt IS NOT NULL ORDER BY upi.lastViewedAt DESC LIMIT :limit")
    List<UserProductInteraction> findRecentlyViewedProductsNotNull(@Param("user") User user, @Param("limit") int limit);

    @Query("SELECT upi.product.id, COUNT(upi) as count FROM UserProductInteraction upi " +
            "GROUP BY upi.product.id ORDER BY count DESC LIMIT :limit")
    List<Object[]> findMostPopularProducts(@Param("limit") int limit);

    @Query("SELECT upi FROM UserProductInteraction upi WHERE upi.user = :user AND " +
            "upi.viewCount > 0 ORDER BY upi.lastViewedAt DESC")
    List<UserProductInteraction> findViewedProductsByUserOrderByRecency(@Param("user") User user);
}
