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

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Voucher v SET v.isValid = false WHERE v.isValid = true AND v.validTo < :now")
    int deactivateExpiredVouchers(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Voucher v SET v.isValid = false WHERE v.isValid = true AND v.usageLimit IS NOT NULL AND v.usedCount >= v.usageLimit")
    int deactivateVouchersReachedLimit();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Voucher v SET v.isValid = false WHERE v.isValid = true AND v.validFrom > :now")
    int deactivateNotStartedVouchers(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Voucher v SET v.isValid = true WHERE v.isValid = false AND v.isActive = true AND (v.validFrom IS NULL OR v.validFrom <= :now) AND (v.validTo IS NULL OR v.validTo >= :now) AND (v.usageLimit IS NULL OR v.usedCount < v.usageLimit)")
    int activateValidVouchers(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);
}
