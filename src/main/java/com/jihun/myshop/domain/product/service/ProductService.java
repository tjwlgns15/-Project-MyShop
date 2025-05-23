package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductImage;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto;
import com.jihun.myshop.domain.product.entity.mapper.ProductMapper;
import com.jihun.myshop.domain.product.event.ProductViewEvent;
import com.jihun.myshop.domain.product.repository.CategoryRepository;
import com.jihun.myshop.domain.product.repository.ProductImageRepository;
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
import com.jihun.myshop.global.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.*;
import static com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.ProductCreateWithImagesDto;
import static com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.ProductUpdateWithImagesDto;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserProductInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    private final ProductMapper productMapper;
    private final DiscountCalculator discountCalculator;

    private final AuthorizationService authorizationService;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public ProductResponseDto createProductWithImages(
            ProductCreateWithImagesDto productCreateDto,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages,
            CustomUserDetails currentUser) {

        // 메인 이미지는 필수
        if (mainImage == null || mainImage.isEmpty()) {
            throw new CustomException(FILE_UPLOAD_ERROR);
        }

        User user = getUserById(currentUser.getId());
        Category category = getCategoryById(productCreateDto.getCategoryId());

        BigDecimal discountPrice = discountCalculator.calculateDiscountPrice(
                productCreateDto.getPrice(),
                productCreateDto.getDiscountType(),
                productCreateDto.getDiscountValue()
        );

        // 상품 생성
        Product product = Product.createProduct(productCreateDto, category, user, discountPrice);
        Product savedProduct = productRepository.save(product);

        // 메인 이미지 저장
        uploadMainImage(savedProduct, mainImage);

        // 추가 이미지 저장
        if (additionalImages != null && !additionalImages.isEmpty()) {
            uploadAdditionalImages(savedProduct, additionalImages, productCreateDto.getAdditionalImagesSortOrder());
        }

        return productMapper.fromEntity(savedProduct);
    }

    @Transactional
    public ProductResponseDto updateProductWithImages(
            Long productId,
            ProductUpdateWithImagesDto productUpdateDto,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages,
            CustomUserDetails userDetails) {

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
        product.updateStatus(productUpdateDto.getProductStatus());

        // 할인 정보 업데이트
        if (productUpdateDto.getPrice() != null || productUpdateDto.getDiscountType() != null || productUpdateDto.getDiscountValue() != null) {
            BigDecimal price = productUpdateDto.getPrice() != null ? productUpdateDto.getPrice() : product.getPrice();
            var discountType = productUpdateDto.getDiscountType() != null ? productUpdateDto.getDiscountType() : product.getDiscountType();
            var discountValue = productUpdateDto.getDiscountValue() != null ? productUpdateDto.getDiscountValue() : product.getDiscountValue();

            BigDecimal calculatedDiscountPrice = discountCalculator.calculateDiscountPrice(price, discountType, discountValue);
            product.updateDiscount(discountType, discountValue, calculatedDiscountPrice);
        }

        // 이미지 처리
        processImagesUpdate(product, productUpdateDto, mainImage, additionalImages);

        return productMapper.fromEntity(product);
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

    public CustomPageResponse<ProductResponseDto> getProductsByCategory(Long categoryId, CustomPageRequest pageRequest) {
        getCategoryById(categoryId);
        Pageable pageable = pageRequest.toPageRequest();

        Page<Product> productPage = productRepository.findByCategoryIdAndProductStatusIn(
                categoryId, List.of(ProductStatus.ACTIVE, ProductStatus.SOLD_OUT), pageable);

        Page<ProductResponseDto> responsePage = productPage.map(productMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

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
    public ProductResponseDto deleteProduct(Long productId, CustomUserDetails userDetails) {
        Product product = getProductById(productId);

        // 권한 검증: ADMIN 또는 판매자 본인만 삭제 가능
        if (!authorizationService.canModifyProduct(userDetails, product)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }

        // 논리적 삭제 처리 (상태를 DELETED로 변경)
        product.markAsDeleted();

        // 이미지 파일은 유지 (물리적 삭제하지 않음)

        return productMapper.fromEntity(product);
    }

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
    private ProductImage uploadMainImage(Product product, MultipartFile mainImage) {
        try {
            // 이미지 파일 저장
            String imageUrl = fileStorageService.storeImage(mainImage);
            String originalFileName = mainImage.getOriginalFilename();
            String storedFileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

            // 이미지 엔티티 생성 및 저장
            ProductImage productImage = ProductImage.createMainImage(
                    product, imageUrl, originalFileName, storedFileName);

            return productImageRepository.save(productImage);
        } catch (Exception e) {
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
    }
    private List<ProductImage> uploadAdditionalImages(
            Product product,
            List<MultipartFile> additionalImages,
            List<Integer> sortOrders) {

        List<ProductImage> savedImages = new ArrayList<>();

        // sortOrders가 null이거나 크기가 맞지 않으면 기본 정렬 순서 사용
        boolean useDefaultSortOrder = sortOrders == null || sortOrders.size() != additionalImages.size();

        // 추가 이미지 처리
        for (int i = 0; i < additionalImages.size(); i++) {
            MultipartFile file = additionalImages.get(i);
            if (file.isEmpty()) continue;

            try {
                // 정렬 순서 결정
                int sortOrder = useDefaultSortOrder ? (i + 1) : sortOrders.get(i);

                // 이미지 파일 저장
                String imageUrl = fileStorageService.storeImage(file);
                String originalFileName = file.getOriginalFilename();
                String storedFileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                // 이미지 엔티티 생성 및 저장
                ProductImage productImage = ProductImage.createAdditionalImage(
                        product, imageUrl, originalFileName, storedFileName, sortOrder);

                savedImages.add(productImageRepository.save(productImage));
            } catch (Exception e) {
                throw new CustomException(FILE_UPLOAD_ERROR);
            }
        }

        return savedImages;
    }
    private void processImagesUpdate(
            Product product,
            ProductUpdateWithImagesDto dto,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages) {

        // 1. 메인 이미지 처리
        if (mainImage != null && !mainImage.isEmpty()) {
            // 기존 메인 이미지 조회
            ProductImage existingMainImage = product.getMainImage();

            if (existingMainImage != null) {
                // 기존 이미지 파일 삭제
                fileStorageService.deleteImage(existingMainImage.getImageUrl());

                // 새 이미지 업로드하고 정보 업데이트
                String imageUrl = fileStorageService.storeImage(mainImage);
                String originalFileName = mainImage.getOriginalFilename();
                String storedFileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

                existingMainImage.updateImageUrl(imageUrl, originalFileName, storedFileName);
            } else {
                // 메인 이미지가 없으면 새로 생성
                uploadMainImage(product, mainImage);
            }
        }

        // 2. 이미지 삭제 처리
        if (dto.getImagesToDelete() != null && !dto.getImagesToDelete().isEmpty()) {
            for (Long imageId : dto.getImagesToDelete()) {
                Optional<ProductImage> imageOpt = product.getImages().stream()
                        .filter(img -> img.getId().equals(imageId) && !img.isMainImage())
                        .findFirst();

                imageOpt.ifPresent(image -> {
                    // 이미지 파일 삭제
                    fileStorageService.deleteImage(image.getImageUrl());

                    // DB에서 이미지 정보 삭제
                    product.removeImage(image);
                    productImageRepository.delete(image);
                });
            }
        }

        // 3. 기존 이미지 정렬 순서 업데이트
        if (dto.getImagesToUpdate() != null && !dto.getImagesToUpdate().isEmpty()) {
            for (ImageToUpdate updateInfo : dto.getImagesToUpdate()) {
                Optional<ProductImage> imageOpt = product.getImages().stream()
                        .filter(img -> img.getId().equals(updateInfo.getId()))
                        .findFirst();

                imageOpt.ifPresent(image -> image.updateSortOrder(updateInfo.getSortOrder()));
            }
        }

        // 4. 새 추가 이미지 업로드
        if (additionalImages != null && !additionalImages.isEmpty()) {
            uploadAdditionalImages(product, additionalImages, dto.getAdditionalImagesSortOrder());
        }
    }
}