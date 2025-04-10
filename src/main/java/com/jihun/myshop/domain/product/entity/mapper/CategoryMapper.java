package com.jihun.myshop.domain.product.entity.mapper;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.dto.CategoryDto;
import org.mapstruct.*;

import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.CategoryDto.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentName", source = "parent.name")
    @Mapping(target = "subcategories", source = "subcategories")
    CategoryResponse fromEntity(Category category);

    List<CategoryResponse> fromEntityList(List<Category> categories);
}