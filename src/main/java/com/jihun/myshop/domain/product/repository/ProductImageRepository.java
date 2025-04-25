package com.jihun.myshop.domain.product.repository;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.jihun.myshop.domain.product.entity.ProductImage.*;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderBySortOrder(Long productId);

    List<ProductImage> findByProductIdAndImageTypeOrderBySortOrder(Long productId, ProductImageType imageType);

    Optional<ProductImage> findByProductIdAndImageType(Long productId, ProductImageType imageType);

    Optional<ProductImage> findByIdAndProductId(Long id, Long productId);

    void deleteAllByProductId(Long productId);

    List<ProductImage> findByProductOrderBySortOrder(Product product);
}
