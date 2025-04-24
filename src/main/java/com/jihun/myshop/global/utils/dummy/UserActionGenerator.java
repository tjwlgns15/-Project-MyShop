package com.jihun.myshop.global.utils.dummy;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.CartItem;
import com.jihun.myshop.domain.cart.event.CartItemAddedEvent;
import com.jihun.myshop.domain.cart.repository.CartRepository;
import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderItem;
import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.event.OrderCompletedEvent;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.Review;
import com.jihun.myshop.domain.product.event.ReviewAddedEvent;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.domain.product.repository.ReviewRepository;
import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.repository.UserRepository;
import com.jihun.myshop.domain.wishlist.entity.WishlistItem;
import com.jihun.myshop.domain.wishlist.event.WishlistItemAddedEvent;
import com.jihun.myshop.domain.wishlist.repository.WishlistRepository;
import com.jihun.myshop.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.jihun.myshop.global.exception.ErrorCode.USER_NOT_EXIST;

/**
 * 사용자 활동 데이터 생성기
 * - 주문 5개
 * - 리뷰 2개
 * - 장바구니 3개
 * - 위시리스트 3개
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserActionGenerator {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final AuthenticationProvider customAuthenticationProvider;
    private final ApplicationEventPublisher eventPublisher;

    // 생성할 데이터 수
    private static final int ORDER_COUNT = 5;
    private static final int REVIEW_COUNT = 2;
    private static final int CART_COUNT = 3;
    private static final int WISHLIST_COUNT = 3;

    /**
     * user 계정으로 활동 데이터 생성
     * @return 생성된 데이터 요약
     */
    @Transactional
    public Map<String, Object> generateUserActions() {
        // 고정 사용자 정보
        String username = "user";
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

        // 임의의 상품 목록 가져오기
        List<Product> randomProducts = getRandomProducts(ORDER_COUNT + REVIEW_COUNT + CART_COUNT + WISHLIST_COUNT);
        if (randomProducts.size() < ORDER_COUNT + REVIEW_COUNT + CART_COUNT + WISHLIST_COUNT) {
            log.warn("충분한 상품이 없습니다. 일부 작업만 수행합니다.");
        }

        Map<String, Object> results = new HashMap<>();
        int index = 0;

        // 1. 주문 생성
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < Math.min(ORDER_COUNT, randomProducts.size()); i++) {
            Product product = randomProducts.get(index++);
            Order order = createOrder(user, product);
            orders.add(order);
        }
        results.put("orders", orders.size());

        // 2. 리뷰 생성 (주문한 상품 중 2개에 리뷰 작성)
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < Math.min(REVIEW_COUNT, orders.size()); i++) {
            Order order = orders.get(i);
            Product product = order.getOrderItems().get(0).getProduct();
            Review review = createReview(user, product, 4 + i % 2); // 4점 또는 5점 리뷰
            reviews.add(review);
        }
        results.put("reviews", reviews.size());

        // 3. 장바구니 추가
        List<Product> cartProducts = new ArrayList<>();
        for (int i = 0; i < Math.min(CART_COUNT, randomProducts.size() - index); i++) {
            Product product = randomProducts.get(index++);
            addToCart(user, product);
            cartProducts.add(product);
        }
        results.put("cartItems", cartProducts.size());

        // 4. 위시리스트 추가
        List<Product> wishlistProducts = new ArrayList<>();
        for (int i = 0; i < Math.min(WISHLIST_COUNT, randomProducts.size() - index); i++) {
            Product product = randomProducts.get(index++);
            addToWishlist(user, product);
            wishlistProducts.add(product);
        }
        results.put("wishlistItems", wishlistProducts.size());

        log.info("사용자 '{}' 활동 데이터 생성 완료: 주문 {}, 리뷰 {}, 장바구니 {}, 위시리스트 {}",
                username, orders.size(), reviews.size(), cartProducts.size(), wishlistProducts.size());

        return results;
    }

    private void login(String username, String password) {
        // Spring Security의 AuthenticationProvider를 사용하여 로그인
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = customAuthenticationProvider.authenticate(authenticationToken);

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<Product> getRandomProducts(int count) {
        // 전체 상품 수 확인
        long totalProducts = productRepository.count();
        if (totalProducts == 0) {
            throw new RuntimeException("상품이 없습니다. 먼저 상품 데이터를 생성해주세요.");
        }

        // 랜덤 상품 선택 (중복 없이)
        Set<Long> selectedIds = new HashSet<>();
        List<Product> selectedProducts = new ArrayList<>();
        Random random = new Random();

        // 페이징을 사용하여 랜덤하게 상품 가져오기
        int pageSize = 10;
        int attempts = 0;
        int maxAttempts = 50; // 무한 루프 방지

        while (selectedProducts.size() < count && attempts < maxAttempts) {
            int pageNumber = random.nextInt((int) (totalProducts / pageSize) + 1);
            Page<Product> productPage = productRepository.findAll(PageRequest.of(pageNumber, pageSize));

            for (Product product : productPage.getContent()) {
                if (selectedIds.add(product.getId())) {
                    selectedProducts.add(product);
                    if (selectedProducts.size() >= count) {
                        break;
                    }
                }
            }
            attempts++;
        }

        return selectedProducts;
    }

    private Order createOrder(User user, Product product) {
        // 주소 생성
        Address address = createAddress(user);

        // 주문 생성
        Order order = Order.createOrder(
                user,
                address,  // 배송 주소
                address,  // 결제 주소 (동일)
                new BigDecimal("3000")  // 배송비
        );

        // 주문 상품 추가
        OrderItem orderItem = OrderItem.createOrderItem(product, 1);
        order.addOrderItem(orderItem);

        // 상품 재고 감소
        product.updateStockQuantity(product.getStockQuantity() - 1);

        // 주문 상태 설정 (결제 완료)
        order.updateOrderStatus(OrderStatus.PAID);
        order.updatePaidAt(LocalDateTime.now().minusDays(new Random().nextInt(14))); // 최근 2주 내 랜덤 날짜

        // 저장
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성: 주문번호 {}, 상품 {}", savedOrder.getOrderNumber(), product.getName());

        // 주문 완료 이벤트 발행 (추천 시스템 이벤트 발행 테스트용)
         eventPublisher.publishEvent(new OrderCompletedEvent(savedOrder));

        return savedOrder;
    }

    private Address createAddress(User user) {
        return Address.builder()
                .user(user)
                .recipientName(user.getName())
                .zipCode("12345")
                .address1("서울시 강남구 테헤란로 123")
                .address2("456동 789호")
                .phone(user.getPhone())
                .isDefault(true)
                .build();
    }

    private Review createReview(User user, Product product, int rating) {
        // 리뷰 내용 템플릿
        List<String> reviewComments = Arrays.asList(
                "배송이 빨라서 좋았습니다. 상품도 기대한대로 좋네요.",
                "가격 대비 성능이 뛰어납니다. 다음에도 구매할 것 같아요.",
                "디자인이 정말 마음에 들어요. 사용감도 좋습니다.",
                "오래 고민했는데 구매하길 잘했어요. 만족합니다.",
                "품질이 생각보다 좋아서 놀랐어요. 추천합니다!"
        );

        Random random = new Random();
        String comment = reviewComments.get(random.nextInt(reviewComments.size()));

        // 리뷰 생성
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .comment(comment)
                .build();

        // 상품에 리뷰 추가
        product.addReview(review);

        // 저장
        Review savedReview = reviewRepository.save(review);
        log.info("리뷰 작성: 상품 {}, 평점 {}", product.getName(), rating);

         eventPublisher.publishEvent(new ReviewAddedEvent(savedReview));

        return savedReview;
    }

    private void addToCart(User user, Product product) {
        // 사용자 장바구니 조회 (데이터베이스에서 직접 조회)
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    // 장바구니가 없는 경우에만 새로 생성
                    Cart newCart = Cart.builder()
                            .user(user)
                            .totalPrice(BigDecimal.ZERO)
                            .build();
                    return cartRepository.save(newCart);
                });

        // 장바구니에 상품 추가
        cart.addItem(product, 1);
        cartRepository.save(cart);
        log.info("장바구니 추가: 상품 {}", product.getName());

         CartItem cartItem = cart.getItems().stream()
                 .filter(item -> item.getProduct().getId().equals(product.getId()))
                 .findFirst()
                 .orElseThrow();
         eventPublisher.publishEvent(new CartItemAddedEvent(cart, cartItem));
    }

    private void addToWishlist(User user, Product product) {
        // 이미 위시리스트에 있는지 확인
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            log.info("이미 위시리스트에 존재: 상품 {}", product.getName());
            return;
        }

        // 위시리스트에 추가
        WishlistItem wishlistItem = new WishlistItem(user, product);
        wishlistRepository.save(wishlistItem);
        log.info("위시리스트 추가: 상품 {}", product.getName());

         eventPublisher.publishEvent(new WishlistItemAddedEvent(wishlistItem));
    }
}