package com.jihun.myshop.domain.recommendation.entity.mapper;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.recommendation.entity.dto.RecommendationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "categoryId", source = "category", qualifiedByName = "getCategoryId")
    @Mapping(target = "categoryName", source = "category", qualifiedByName = "getCategoryName")
    RecommendationResponseDto fromEntity(Product product);

    @Named("getCategoryId")
    default Long getCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }

    @Named("getCategoryName")
    default String getCategoryName(Category category) {
        return category != null ? category.getName() : null;
    }
}
