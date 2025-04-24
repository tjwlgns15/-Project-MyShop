package com.jihun.myshop.domain.product.repository;

import com.jihun.myshop.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Dictionary;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);

    Optional<Category> findByName(String name);

}
