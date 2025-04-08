package com.jihun.myshop.global.security.listener;

import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.Role;
import com.jihun.myshop.domain.user.entity.RoleHierarchy;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.dto.UserSignupDto;
import com.jihun.myshop.domain.user.entity.mapper.UserMapper;
import com.jihun.myshop.domain.user.repository.AddressRepository;
import com.jihun.myshop.domain.user.repository.RoleHierarchyRepository;
import com.jihun.myshop.domain.user.repository.RoleRepository;
import com.jihun.myshop.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleHierarchyRepository roleHierarchyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;

    private boolean alreadySetup = false;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        setupData();
        alreadySetup = true;
    }

    private void setupData() {
        // 역할 생성
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        Role dbaRole = createRoleIfNotFound("ROLE_DBA", "데이터베이스 관리자");
        Role userRole = createRoleIfNotFound("ROLE_USER", "사용자");

        // 사용자 생성
        User admin = createUserIfNotFound(createSignupDto("admin", "1234", "관리자", "01012345678"), adminRole);
        User manager = createUserIfNotFound(createSignupDto("manager", "1234", "매니저", "01012345678"), managerRole);
        User dba = createUserIfNotFound(createSignupDto("dba", "1234", "데이터베이스 관리자", "01012345678"), dbaRole);
        User user = createUserIfNotFound(createSignupDto("user", "1234", "사용자", "01012345678"), userRole);

        // 주소 생성 및 연결
        createDefaultAddressIfNotFound(admin, "관리자", "12345", "대전시 서구", "대덕대로 123", "01012345678");
        createDefaultAddressIfNotFound(manager, "매니저", "12345", "대전시 중구", "대덕대로 456", "01012345678");
        createDefaultAddressIfNotFound(dba, "데이터베이스 관리자", "12345", "대전시 유성구", "대덕대로 789", "01012345678");
        createDefaultAddressIfNotFound(user, "사용자", "12345", "대전시 동구", "대덕대로 101", "01012345678");

        // 역할 계층 구조 설정
        RoleHierarchy adminRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_ADMIN", null);
        RoleHierarchy managerRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_MANAGER", adminRoleHierarchy);
        RoleHierarchy dbaRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_DBA", adminRoleHierarchy);
        RoleHierarchy userRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_USER", managerRoleHierarchy);
    }

    /**
     * UserSignupDto 생성 메서드 - address 필드 제거
     */
    private UserSignupDto createSignupDto(String username, String password, String name, String phone) {
        UserSignupDto dto = new UserSignupDto();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setName(name);
        dto.setPhone(phone);
        return dto;
    }

    public Role createRoleIfNotFound(String roleName, String roleDesc) {
        Role role = roleRepository.findByRoleName(roleName).orElseGet(() -> Role.builder()
                .roleName(roleName)
                .roleDesc(roleDesc)
                .build()
        );

        return roleRepository.save(role);
    }

    public User createUserIfNotFound(UserSignupDto signupDto, Role role) {
        // 사용자 이름으로 기존 사용자 검색
        return userRepository.findByUsername(signupDto.getUsername()).orElseGet(() -> {
            // SignupDto를 User 엔티티로 변환 - MapStruct 사용
            User newUser = userMapper.signupDtoToUser(signupDto);

            // 역할 추가 - addRole 메서드 활용
            newUser.addRole(role);

            // 사용자 저장
            return userRepository.save(newUser);
        });
    }

    /**
     * 사용자의 기본 주소 생성
     */
    public void createDefaultAddressIfNotFound(User user, String recipientName, String zipCode,
                                               String address1, String address2, String phone) {
        // 주소 목록이 null인 경우 처리
        if (user.getAddresses() == null) {
            // 새 주소 생성
            Address address = Address.builder()
                    .user(user)
                    .recipientName(recipientName)
                    .zipCode(zipCode)
                    .address1(address1)
                    .address2(address2)
                    .phone(phone)
                    .isDefault(true)
                    .build();

            addressRepository.save(address);
            return;
        }

        // 이미 기본 주소가 있는지 확인
        boolean hasDefaultAddress = user.getAddresses().stream()
                .anyMatch(Address::isDefault);

        if (!hasDefaultAddress) {
            Address address = Address.builder()
                    .user(user)
                    .recipientName(recipientName)
                    .zipCode(zipCode)
                    .address1(address1)
                    .address2(address2)
                    .phone(phone)
                    .isDefault(true)
                    .build();

            addressRepository.save(address);
        }
    }

    public RoleHierarchy createRoleHierarchyIfNotFound(String roleName, RoleHierarchy parent) {
        return roleHierarchyRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    RoleHierarchy newRoleHierarchy = RoleHierarchy.builder()
                            .roleName(roleName)
                            .build();

                    if (parent != null) {
                        newRoleHierarchy.setParent(parent);
                    }

                    return roleHierarchyRepository.save(newRoleHierarchy);
                });
    }
}