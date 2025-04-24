package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.entity.mapper.ProductMapper;
import com.jihun.myshop.domain.product.event.ProductViewEvent;
import com.jihun.myshop.domain.product.repository.CategoryRepository;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.recommendation.entity.UserProductInteraction;
import com.jihun.myshop.domain.recommendation.repository.UserProductInteractionRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import com.jihun.myshop.global.security.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.myshop.domain.product.entity.dto.ProductDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserProductInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final DiscountCalculator discountCalculator;

    private final AuthorizationService authorizationService;
    private final ApplicationEventPublisher eventPublisher;


    private Product getProductById(Long productId) {
        return productRepository.findByIdWithCategoryAndSeller(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
    }
    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));
    }
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
    }
    private List<Long> collectCategoryIds(Category category) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(category.getId());

        // 하위 카테고리가 있으면 재귀적으로 모든 ID 수집
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            for (Category subcategory : category.getSubcategories()) {
                categoryIds.addAll(collectCategoryIds(subcategory));
            }
        }
        return categoryIds;
    }

    @Transactional
    public ProductResponseDto createProduct(ProductCreateDto productCreateDto, CustomUserDetails currentUser) {
        User user = getUserById(currentUser.getId());
        Category category = getCategoryById(productCreateDto.getCategoryId());

        BigDecimal discountPrice = discountCalculator.calculateDiscountPrice(
                productCreateDto.getPrice(),
                productCreateDto.getDiscountType(),
                productCreateDto.getDiscountValue()
        );

        // todo: 변환 메서드? 생성자?
        Product product = productMapper.fromCreateDto(productCreateDto, category, user, discountPrice);
        Product saveProduct = productRepository.save(product);

        return productMapper.fromEntity(saveProduct);
    }

    @Transactional
    public ProductResponseDto getProduct(Long productId, CustomUserDetails currentUser) {
        Product product = getProductById(productId);

        if (currentUser != null) {
            eventPublisher.publishEvent(new ProductViewEvent(currentUser, productId));
        }
        return productMapper.fromEntity(product);
    }

    public CustomPageResponse<ProductResponseDto> getProducts(CustomPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageRequest();

        Page<Product> productPage = productRepository.findByProductStatusIn(
                List.of(ProductStatus.ACTIVE, ProductStatus.SOLD_OUT), pageable);

        Page<ProductResponseDto> responsePage = productPage.map(productMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    // 페이징 적용한 카테고리별 상품 조회
    public CustomPageResponse<ProductResponseDto> getProductsByCategory(Long categoryId, CustomPageRequest pageRequest) {
        getCategoryById(categoryId);
        Pageable pageable = pageRequest.toPageRequest();

        Page<Product> productPage = productRepository.findByCategoryIdAndProductStatusIn(
                categoryId, List.of(ProductStatus.ACTIVE, ProductStatus.SOLD_OUT), pageable);

        Page<ProductResponseDto> responsePage = productPage.map(productMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    // 페이징 적용한 카테고리 및 하위 카테고리 상품 조회
    public CustomPageResponse<ProductResponseDto> getProductsByCategoryIncludingSubcategories(Long categoryId, CustomPageRequest pageRequest) {
        Category category = getCategoryById(categoryId);
        List<Long> categoryIds = collectCategoryIds(category);

        Pageable pageable = pageRequest.toPageRequest();

        Page<Product> productPage = productRepository.findByCategoryIdInAndProductStatusIn(
                categoryIds, List.of(ProductStatus.ACTIVE, ProductStatus.SOLD_OUT), pageable);

        Page<ProductResponseDto> responsePage = productPage.map(productMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductUpdateDto productUpdateDto, CustomUserDetails userDetails) {
        Product product = getProductById(productId);

        // 권한 검증: ADMIN 또는 판매자 본인만 수정 가능
        if (!authorizationService.canModifyProduct(userDetails, product)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }

        Category category = getCategoryById(productUpdateDto.getCategoryId());

        // 기본 정보 업데이트
        product.updateBasicInfo(productUpdateDto.getName(), productUpdateDto.getDescription(), productUpdateDto.getPrice());
        product.updateCategory(category);
        product.updateStockQuantity(productUpdateDto.getStockQuantity());
        product.updateMainImage(productUpdateDto.getMainImageUrl());
        product.updateStatus(productUpdateDto.getProductStatus());

        // 할인 정보 업데이트
        if (productUpdateDto.getPrice() != null || productUpdateDto.getDiscountType() != null || productUpdateDto.getDiscountValue() != null) {
            BigDecimal price = productUpdateDto.getPrice() != null ? productUpdateDto.getPrice() : product.getPrice();
            var discountType = productUpdateDto.getDiscountType() != null ? productUpdateDto.getDiscountType() : product.getDiscountType();
            var discountValue = productUpdateDto.getDiscountValue() != null ? productUpdateDto.getDiscountValue() : product.getDiscountValue();

            BigDecimal calculatedDiscountPrice = discountCalculator.calculateDiscountPrice(price, discountType, discountValue);
            product.updateDiscount(discountType, discountValue, calculatedDiscountPrice);
        }

        return productMapper.fromEntity(product);
    }

    @Transactional
    public ProductResponseDto deleteProduct(Long productId, CustomUserDetails userDetails) {
        Product product = getProductById(productId);

        // 권한 검증: ADMIN 또는 판매자 본인만 삭제 가능
        if (!authorizationService.canModifyProduct(userDetails, product)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }

        // 논리적 삭제 처리 (상태를 DELETED로 변경)
        product.markAsDeleted();

        return productMapper.fromEntity(product);
    }

    // 판매자의 상품 조회
    public CustomPageResponse<ProductResponseDto> getSellerProducts(CustomUserDetails currentUser, CustomPageRequest pageRequest) {
        User seller = getUserById(currentUser.getId());
        Pageable pageable = pageRequest.toPageRequest();

        Page<Product> productPage = productRepository.findBySellerId(seller.getId(), pageable);
        Page<ProductResponseDto> responsePage = productPage.map(productMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    public CustomPageResponse<ProductResponseDto> getSellerProductsByStatus(CustomUserDetails currentUser, List<ProductStatus> statuses, CustomPageRequest pageRequest) {
        User seller = getUserById(currentUser.getId());
        Pageable pageable = pageRequest.toPageRequest();

        Page<Product> productPage = productRepository.findBySellerIdAndProductStatusIn(seller.getId(), statuses, pageable);
        Page<ProductResponseDto> responsePage = productPage.map(productMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    public List<ProductResponseDto> getRecentlyViewedProducts(CustomUserDetails currentUser, int limit) {
        User user = getUserById(currentUser.getId());

        List<UserProductInteraction> recentlyViewedProducts = interactionRepository.findRecentlyViewedProductsNotNull(user, limit);

        return recentlyViewedProducts.stream()
                .map(UserProductInteraction::getProduct)
                .map(productMapper::fromEntity)
                .collect(Collectors.toList());
    }
}
