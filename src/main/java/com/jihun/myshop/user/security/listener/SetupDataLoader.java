package com.jihun.myshop.user.security.listener;

import com.jihun.myshop.user.entity.Role;
import com.jihun.myshop.user.entity.RoleHierarchy;
import com.jihun.myshop.user.entity.User;
import com.jihun.myshop.user.repository.RoleHierarchyRepository;
import com.jihun.myshop.user.repository.RoleRepository;
import com.jihun.myshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleHierarchyRepository roleHierarchyRepository;
    private boolean alreadySetup = false;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    
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
        HashSet<Role> roles1 = new HashSet<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles1.add(adminRole);
        createUserIfNotFound("admin", "1234", "관리자", "대전", "01012345678", roles1);

        HashSet<Role> roles2 = new HashSet<>();
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        roles2.add(managerRole);
        createUserIfNotFound("manager", "1234", "매니저", "대전", "01012345678", roles2);

        HashSet<Role> roles3 = new HashSet<>();
        Role dbRole = createRoleIfNotFound("ROLE_DBA", "데이터베이스 관리자");
        roles3.add(dbRole);
        createUserIfNotFound("dba", "1234", "데이터베이스 관리자", "대전", "01012345678", roles3);

        HashSet<Role> roles4 = new HashSet<>();
        Role userRole = createRoleIfNotFound("ROLE_USER", "사용자");
        roles4.add(userRole);
        createUserIfNotFound("user", "1234", "사용자", "대전", "01012345678", roles4);

        // 역할 계층 구조 설정
        RoleHierarchy adminRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_ADMIN", null);
        RoleHierarchy managerRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_MANAGER", adminRoleHierarchy);
        RoleHierarchy dbaRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_DBA", adminRoleHierarchy);
        RoleHierarchy userRoleHierarchy = createRoleHierarchyIfNotFound("ROLE_USER", managerRoleHierarchy);
    }


    public Role createRoleIfNotFound(String roleName, String roleDesc) {
        Role role = roleRepository.findByRoleName(roleName).orElseGet(() -> Role.builder()
                .roleName(roleName)
                .roleDesc(roleDesc)
                .build()
        );

        return roleRepository.save(role);
    }

    public void createUserIfNotFound(String username, String password, String name, String address, String phone, Set<Role> roleSet) {
        User newUser = userRepository.findByUsername(username).orElseGet(() -> User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .address(address)
                .phone(phone)
                .userRoles(roleSet)
                .build()
        );

        userRepository.save(newUser);
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