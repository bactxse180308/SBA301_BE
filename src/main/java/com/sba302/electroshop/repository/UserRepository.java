package com.sba302.electroshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM User u WHERE u.registrationDate >= :startOfDay AND u.registrationDate < :endOfDay AND u.role.roleName = 'CUSTOMER'")
    Integer countNewCustomersByDateRange(@org.springframework.data.repository.query.Param("startOfDay") java.time.LocalDateTime startOfDay, @org.springframework.data.repository.query.Param("endOfDay") java.time.LocalDateTime endOfDay);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = 'CUSTOMER'")
    Integer countTotalCustomers();
}
