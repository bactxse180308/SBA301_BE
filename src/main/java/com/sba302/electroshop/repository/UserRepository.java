package com.sba302.electroshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT u FROM User u JOIN FETCH u.role " +
           "WHERE u.role.roleName = 'CUSTOMER' " +
           "AND (LOWER(u.fullName) LIKE :keyword OR LOWER(u.email) LIKE :keyword OR u.phoneNumber LIKE :keyword)")
    List<User> searchCustomersByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
