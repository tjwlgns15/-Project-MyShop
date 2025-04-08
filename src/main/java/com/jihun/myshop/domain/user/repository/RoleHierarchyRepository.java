package com.jihun.myshop.domain.user.repository;

import com.jihun.myshop.domain.user.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
    Optional<RoleHierarchy> findByRoleName(String roleName);
}
