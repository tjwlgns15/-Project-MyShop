package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.mapper.CategoryMapper;
import com.jihun.myshop.domain.product.repository.CategoryRepository;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.CategoryDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.BED_REQUEST;
import static com.jihun.myshop.global.exception.ErrorCode.CATEGORY_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;


    @Transactional
    public CategoryResponse createCategory(CategoryCreate dto) {

        // todo: 관리자 권한 확인
        Category parentCategory = null;
        if (dto.getParentId() != null) {
            parentCategory = getCategoryById(dto.getParentId());
        }

        // 카테고리 생성
        Category newCategory = Category.createNewCategory(
                dto.getName(),
                dto.getDescription(),
                parentCategory
        );

        Category savedCategory = categoryRepository.save(newCategory);
        return categoryMapper.fromEntity(savedCategory);
    }

    public CategoryResponse getCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        return categoryMapper.fromEntity(category);
    }

    // 루트 카테고리만 조회 (부모가 없는 카테고리)
    public List<CategoryResponse> getRootCategories() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        return categoryMapper.fromEntityList(rootCategories);
    }

    // 특정 카테고리의 하위 카테고리 조회
    public List<CategoryResponse> getSubcategories(Long parentId) {
        Category parentCategory = getCategoryById(parentId);
        return categoryMapper.fromEntityList(parentCategory.getSubcategories());
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdate dto) {
        Category category = getCategoryById(categoryId);

        // 자기 자신을 부모로 설정하는 것을 방지
        if (categoryId.equals(dto.getParentId())) {
            throw new CustomException(BED_REQUEST);
        }

        Category parentCategory = null;
        if (dto.getParentId() != null) {
            parentCategory = getCategoryById(dto.getParentId());

            // 순환 참조 방지 (현재 카테고리의 하위 카테고리를 부모로 설정하는 것을 방지)
            if (isDescendant(parentCategory, category)) {
                throw new CustomException(BED_REQUEST);
            }
        }

        // 카테고리 업데이트
        category.update(dto.getName(), dto.getDescription(), parentCategory);

        return categoryMapper.fromEntity(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }


    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));
    }

    // 주어진 카테고리가 대상 카테고리의 하위 카테고리인지 확인
    private boolean isDescendant(Category potentialDescendant, Category target) {
        if (potentialDescendant == null) {
            return false;
        }

        if (target.getId().equals(potentialDescendant.getId())) {
            return true;
        }

        for (Category child : target.getSubcategories()) {
            if (isDescendant(potentialDescendant, child)) {
                return true;
            }
        }

        return false;
    }

}
