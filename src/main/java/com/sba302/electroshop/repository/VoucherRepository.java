package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Optional<Voucher> findByVoucherCode(String code);

    @org.springframework.data.jpa.repository.Query("SELECT v FROM Voucher v WHERE LOWER(v.voucherCode) LIKE :keyword OR LOWER(v.description) LIKE :keyword")
    java.util.List<Voucher> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
}
