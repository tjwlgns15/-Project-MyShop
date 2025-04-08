package com.jihun.myshop.domain.user.repository;

import com.jihun.myshop.domain.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
