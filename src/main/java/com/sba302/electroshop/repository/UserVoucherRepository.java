package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.UserVoucher;
import com.sba302.electroshop.entity.UserVoucherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, UserVoucherId> {
}
