package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.CustomerVoucher;
import com.sba302.electroshop.entity.CustomerVoucherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, CustomerVoucherId> {
}
