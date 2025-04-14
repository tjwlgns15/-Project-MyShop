package com.jihun.myshop.domain.product.entity.mapper;

import com.jihun.myshop.domain.product.entity.Category;
import org.mapstruct.*;

import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.CategoryDto.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    @Mapping(target = "subcategories", source = "subcategories")
    CategoryResponseDto fromEntity(Category category);

    List<CategoryResponseDto> fromEntityList(List<Category> categories);
}