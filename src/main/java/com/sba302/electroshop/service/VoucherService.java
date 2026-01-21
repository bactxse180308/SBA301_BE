package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Voucher;
import java.util.List;
import java.util.Optional;

public interface VoucherService {
    List<Voucher> findAll();

    Optional<Voucher> findById(Integer id);

    Voucher save(Voucher voucher);

    void deleteById(Integer id);
}
