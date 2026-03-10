package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Integer> {

    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v WHERE uv.user.userId = :userId AND v.voucherCode = :code")
    Optional<UserVoucher> findByUserIdAndVoucherCode(@Param("userId") Integer userId, @Param("code") String code);

    Optional<UserVoucher> findByUser_UserIdAndVoucher_VoucherCode(Integer userId, String voucherCode);

    @Query("SELECT uv.user.userId FROM UserVoucher uv " +
            "WHERE uv.voucher.voucherId = :voucherId " +
            "AND uv.user.userId IN :userIds")
    List<Integer> findUserIdsByVoucherIdAndUserIdIn(
            @Param("voucherId") Integer voucherId,
            @Param("userIds") List<Integer> userIds
    );

    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher WHERE uv.user.userId = :userId")
    org.springframework.data.domain.Page<UserVoucher> findByUserId(@Param("userId") Integer userId, org.springframework.data.domain.Pageable pageable);
}
