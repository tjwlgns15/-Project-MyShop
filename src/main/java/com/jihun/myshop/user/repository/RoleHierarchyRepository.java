package com.jihun.myshop.user.repository;

import com.jihun.myshop.user.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
    Optional<RoleHierarchy> findByRoleName(String roleName);
}
