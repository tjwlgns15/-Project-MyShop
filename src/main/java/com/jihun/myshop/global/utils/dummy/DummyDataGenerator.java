package com.jihun.myshop.global.utils.dummy;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.DiscountType;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.repository.CategoryRepository;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.jihun.myshop.global.exception.ErrorCode.USER_NOT_EXIST;

/**
 * 더미 데이터 생성기
 * <p>
 * 실행 방법:
 * 1. 컨트롤러에서 API 호출을 통해 실행
 * 2. seller 계정과 비밀번호 1234로 로그인 후 데이터 생성
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DummyDataGenerator {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuthenticationProvider customAuthenticationProvider;

    // 카테고리 및 하위 카테고리 정의
    private static final Map<String, List<String>> CATEGORY_MAP = Map.of(
            "전자제품", List.of("스마트폰", "노트북", "태블릿", "스마트워치", "이어폰/헤드폰"),
            "의류", List.of("남성의류", "여성의류", "아동의류", "스포츠웨어", "액세서리"),
            "가구/인테리어", List.of("소파", "침대", "테이블/의자", "조명", "수납/정리"),
            "식품", List.of("신선식품", "가공식품", "건강식품", "음료", "간식"),
            "뷰티", List.of("스킨케어", "메이크업", "헤어케어", "바디케어", "향수"),
            "도서", List.of("소설", "자기계발", "경제/경영", "IT/컴퓨터", "예술/대중문화")
    );

    // 제품명 접두사 및 접미사
    private static final List<String> PRODUCT_PREFIXES = List.of(
            "프리미엄", "고급", "클래식", "NEW", "베스트", "트렌디", "슈퍼", "울트라", "스페셜", "에코"
    );

    private static final List<String> PRODUCT_SUFFIXES = List.of(
            "에디션", "시리즈", "컬렉션", "라인", "패키지", "세트", "MAX", "PRO", "LITE", "PLUS"
    );

    // 상품 설명 템플릿
    private static final List<String> DESCRIPTION_TEMPLATES = List.of(
            "최고의 품질로 제작된 %s입니다. 내구성이 뛰어나고 사용하기 편리합니다.",
            "%s의 새로운 버전으로, 이전 모델보다 성능이 20%% 향상되었습니다.",
            "인기 상품 %s가 할인된 가격으로 돌아왔습니다. 놓치지 마세요!",
            "편안함과 스타일을 동시에 갖춘 %s, 일상 생활에 완벽한 선택입니다.",
            "혁신적인 디자인의 %s, 현대적인 라이프스타일에 꼭 맞는 제품입니다.",
            "다양한 용도로 활용 가능한 %s, 실용성과 미적 감각을 모두 만족시킵니다.",
            "장인 정신으로 제작된 %s, 섬세한 디테일과 뛰어난 기능성을 경험하세요.",
            "친환경 소재로 제작된 %s, 환경을 생각하는 소비자들을 위한 선택입니다.",
            "한정판 %s, 독특한 디자인과 특별한 기능을 갖추고 있습니다.",
            "연구소에서 개발된 기술력의 결정체, %s를 지금 만나보세요."
    );

    // 이미지 URL 템플릿
    private static final List<String> IMAGE_URL_TEMPLATES = List.of(
            "https://picsum.photos/seed/%d/800/600",
            "https://loremflickr.com/800/600/product?lock=%d",
            "https://source.unsplash.com/random/800x600?product&sig=%d"
    );

    /**
     * 더미 데이터 생성을 실행하는 메서드
     *
     * @return 생성된 상품 수
     */
    @Transactional
    public int generateDummyData() {
        // 고정 사용자 정보
        String username = "seller";
        String password = "1234";

        // 로그인 처리
        try {
            login(username, password);
            log.info("로그인 성공: {}", username);
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new RuntimeException("로그인 실패: " + e.getMessage());
        }

        // 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        // 카테고리 생성
        Map<String, Category> mainCategories = createMainCategories();
        Map<String, Category> subCategories = createSubCategories(mainCategories);

        // 상품 생성
        int productCount = createProducts(subCategories, user);
        log.info("총 {}개의 더미 상품 생성 완료", productCount);

        return productCount;
    }

    private void login(String username, String password) {
        // Spring Security의 AuthenticationProvider를 사용하여 로그인
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = customAuthenticationProvider.authenticate(authenticationToken);

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Map<String, Category> createMainCategories() {
        Map<String, Category> mainCategories = new HashMap<>();

        for (String categoryName : CATEGORY_MAP.keySet()) {
            if (categoryRepository.findByName(categoryName).isEmpty()) {
                Category category = Category.createNewCategory(
                        categoryName,
                        categoryName + " 카테고리입니다.",
                        null
                );
                categoryRepository.save(category);
                mainCategories.put(categoryName, category);
                log.info("카테고리 생성: {}", categoryName);
            } else {
                mainCategories.put(categoryName, categoryRepository.findByName(categoryName).get());
                log.info("기존 카테고리 사용: {}", categoryName);
            }
        }

        return mainCategories;
    }

    private Map<String, Category> createSubCategories(Map<String, Category> mainCategories) {
        Map<String, Category> subCategories = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : CATEGORY_MAP.entrySet()) {
            String mainCategoryName = entry.getKey();
            Category mainCategory = mainCategories.get(mainCategoryName);

            for (String subCategoryName : entry.getValue()) {
                if (categoryRepository.findByName(subCategoryName).isEmpty()) {
                    Category subCategory = Category.createNewCategory(
                            subCategoryName,
                            mainCategoryName + "의 하위 카테고리인 " + subCategoryName + "입니다.",
                            mainCategory
                    );
                    categoryRepository.save(subCategory);
                    subCategories.put(subCategoryName, subCategory);
                    log.info("하위 카테고리 생성: {} (상위: {})", subCategoryName, mainCategoryName);
                } else {
                    subCategories.put(subCategoryName, categoryRepository.findByName(subCategoryName).get());
                    log.info("기존 하위 카테고리 사용: {} (상위: {})", subCategoryName, mainCategoryName);
                }
            }
        }

        return subCategories;
    }

    private int createProducts(Map<String, Category> subCategories, User seller) {
        Random random = new Random();
        int totalProductCount = 0;

        // 각 하위 카테고리별 5~15개의 상품 생성
        for (Category category : subCategories.values()) {
            int productCount = random.nextInt(11) + 5; // 5~15 사이의 상품 수
            int createdCount = 0;

            for (int i = 0; i < productCount; i++) {
                String productName = generateProductName(category.getName());

                // 이미 존재하는 상품인지 확인
                if (productRepository.findByName(productName).isPresent()) {
                    continue;
                }

                // 가격 설정 (10,000원 ~ 1,000,000원)
                BigDecimal price = BigDecimal.valueOf(random.nextInt(990000) + 10000)
                        .setScale(0, RoundingMode.DOWN);

                // 할인 여부 결정 (30% 확률로 할인)
                BigDecimal discountPrice = null;
                DiscountType discountType = null;
                BigDecimal discountValue = null;

                if (random.nextDouble() < 0.3) {
                    discountType = random.nextBoolean() ? DiscountType.PERCENTAGE : DiscountType.FIXED_AMOUNT;

                    if (discountType == DiscountType.PERCENTAGE) {
                        discountValue = BigDecimal.valueOf(random.nextInt(40) + 5) // 5~45% 할인
                                .setScale(0, RoundingMode.DOWN);
                        discountPrice = price.multiply(BigDecimal.ONE.subtract(discountValue.divide(BigDecimal.valueOf(100))))
                                .setScale(0, RoundingMode.DOWN);
                    } else {
                        discountValue = price.multiply(BigDecimal.valueOf(random.nextDouble() * 0.4 + 0.05))
                                .setScale(0, RoundingMode.DOWN); // 5~45% 금액 할인
                        discountPrice = price.subtract(discountValue);
                    }
                }

                // 상품 설명 생성
                String description = String.format(
                        DESCRIPTION_TEMPLATES.get(random.nextInt(DESCRIPTION_TEMPLATES.size())),
                        productName
                );

                // 재고 수량 (10~100)
                int stockQuantity = random.nextInt(91) + 10;

                // 이미지 URL
                String imageUrl = String.format(
                        IMAGE_URL_TEMPLATES.get(random.nextInt(IMAGE_URL_TEMPLATES.size())),
                        random.nextInt(1000)
                );

                // 상품 생성
                Product product = Product.builder()
                        .name(productName)
                        .description(description)
                        .price(price)
                        .discountPrice(discountPrice)
                        .discountType(discountType)
                        .discountValue(discountValue)
                        .stockQuantity(stockQuantity)
                        .mainImageUrl(imageUrl)
                        .category(category)
                        .seller(seller)
                        .productStatus(ProductStatus.ACTIVE)
                        .totalReviews(0)
                        .averageRating(0.0)
                        .build();

                productRepository.save(product);
                createdCount++;
                totalProductCount++;

                log.info("상품 생성: {} (카테고리: {}, 가격: {}, 할인가: {})",
                        productName, category.getName(), price, discountPrice);
            }

            log.info("카테고리 '{}' 상품 {}개 생성 완료", category.getName(), createdCount);
        }

        return totalProductCount;
    }

    private String generateProductName(String categoryName) {
        Random random = new Random();
        StringBuilder nameBuilder = new StringBuilder();

        // 50% 확률로 접두사 추가
        if (random.nextBoolean()) {
            nameBuilder.append(PRODUCT_PREFIXES.get(random.nextInt(PRODUCT_PREFIXES.size())))
                    .append(" ");
        }

        // 카테고리명 포함
        nameBuilder.append(categoryName).append(" ");

        // 고유 식별자 추가 (A-Z + 숫자)
        nameBuilder.append((char) (random.nextInt(26) + 'A'))
                .append(random.nextInt(100));

        // 30% 확률로 접미사 추가
        if (random.nextDouble() < 0.3) {
            nameBuilder.append(" ")
                    .append(PRODUCT_SUFFIXES.get(random.nextInt(PRODUCT_SUFFIXES.size())));
        }

        return nameBuilder.toString();
    }
}