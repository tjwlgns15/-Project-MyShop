package com.jihun.myshop.domain.recommendation.repository;

import com.jihun.myshop.domain.recommendation.entity.ProductRecommendation;
import com.jihun.myshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, Long> {

//    List<ProductRecommendation> findByUserOrderByScoreDesc(User user);
//
//    List<ProductRecommendation> findByUserAndRecommendationTypeOrderByScoreDesc(
//            User user, ProductRecommendation.RecommendationType type);
//
    @Query("SELECT pr FROM ProductRecommendation pr WHERE pr.user = :user AND pr.recommendationType = :type ORDER BY pr.score DESC LIMIT :limit")
    List<ProductRecommendation> findTopNByUserAndType(@Param("user") User user,
                                                      @Param("type") ProductRecommendation.RecommendationType type,
                                                      @Param("limit") int limit);

    void deleteByUser(User user);
}
