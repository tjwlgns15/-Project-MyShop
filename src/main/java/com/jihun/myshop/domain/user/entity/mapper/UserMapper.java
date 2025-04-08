package com.jihun.myshop.domain.user.entity.mapper;

import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.Role;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.dto.AddressDto;
import com.jihun.myshop.domain.user.entity.dto.UserResponse;
import com.jihun.myshop.domain.user.entity.dto.UserSignupDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 모든 User 관련 매핑을 처리하는 통합 매퍼
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {java.util.stream.Collectors.class}
)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * (회원가입) UserSignupDto -> User
     */
    @Mapping(target = "username", expression = "java(signupDto.getUsername())")
    @Mapping(target = "name", expression = "java(signupDto.getName())")
    @Mapping(target = "phone", expression = "java(signupDto.getPhone())")
    @Mapping(target = "password", expression = "java(encodePassword(signupDto.getPassword()))")
    @Mapping(target = "addresses", ignore = true) // 주소는 별도로 처리
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    public abstract User signupDtoToUser(UserSignupDto signupDto);

    /**
     * (유저 정보 반환) User -> UserResponse
     */
    @Named("toResponse")
    @Mapping(target = "username", expression = "java(user.getUsername())")
    @Mapping(target = "name", expression = "java(user.getName())")
    @Mapping(target = "phone", expression = "java(user.getPhone())")
    @Mapping(target = "password", constant = "null") // 비밀번호는 보안상 null
    @Mapping(target = "userRoles", expression = "java(mapRoles(user.getUserRoles()))")
    @Mapping(target = "defaultAddress", expression = "java(findDefaultAddress(user))")
    public abstract UserResponse userToResponse(User user);

    /**
     * (인증에 사용하는 UserResponse) User -> UserResponse
     */
    @Named("toUserDetailsResponse")
    @Mapping(target = "username", expression = "java(user.getUsername())")
    @Mapping(target = "name", expression = "java(user.getName())")
    @Mapping(target = "phone", expression = "java(user.getPhone())")
    @Mapping(target = "password", expression = "java(user.getPassword())") // 비밀번호 유지
    @Mapping(target = "userRoles", expression = "java(mapRoles(user.getUserRoles()))")
    @Mapping(target = "defaultAddress", expression = "java(findDefaultAddress(user))")
    public abstract UserResponse userToUserDetailsResponse(User user);

    /**
     * Role 세트를 문자열 세트로 변환하는 헬퍼 메서드
     */
    protected Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }

    /**
     * 비밀번호 인코딩 메서드
     */
    protected String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * User 업데이트 메서드 - 수동 매핑 구현
     * (User 클래스에 setter가 없어 @MappingTarget을 사용할 수 없음)
     */
    @Named("updateUser")
    public User updateUserFromDto(UserSignupDto dto, User user) {
        // 업데이트할 필드가 있을 경우 빌더를 사용하여 새 객체 생성
        if (dto == null) {
            return user;
        }

        // 빌더를 사용하여 기존 값 유지하면서 필요한 필드만 업데이트
        User.UserBuilder builder = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())  // 기본값 유지
                .name(user.getName())
                .phone(user.getPhone())
                .userRoles(user.getUserRoles())
                .addresses(user.getAddresses())
                .orders(user.getOrders())
                .cart(user.getCart())
                .reviews(user.getReviews())
                .wishlistItems(user.getWishlistItems());

        // DTO에서 제공된 값으로 업데이트
        if (dto.getName() != null) {
            builder.name(dto.getName());
        }
        if (dto.getPhone() != null) {
            builder.phone(dto.getPhone());
        }
        if (dto.getPassword() != null) {
            builder.password(encodePassword(dto.getPassword()));
        }

        return builder.build();
    }

    /**
     * 사용자 목록을 응답 DTO 목록으로 변환
     */
    public abstract java.util.List<UserResponse> usersToResponseList(java.util.List<User> users);

    protected AddressDto findDefaultAddress(User user) {
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            return null;
        }

        // 기본 주소 찾기
        return user.getAddresses().stream()
                .filter(Address::isDefault)
                .findFirst()
                .map(this::addressToDto)
                .orElse(null);
    }

    protected AddressDto addressToDto(Address address) {
        if (address == null) {
            return null;
        }

        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setRecipientName(address.getRecipientName());
        dto.setZipCode(address.getZipCode());
        dto.setAddress1(address.getAddress1());
        dto.setAddress2(address.getAddress2());
        dto.setPhone(address.getPhone());
        dto.setDefault(address.isDefault());

        return dto;
    }

}