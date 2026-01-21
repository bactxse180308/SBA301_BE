package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Voucher;
import com.sba302.electroshop.repository.VoucherRepository;
import com.sba302.electroshop.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;

    @Override
    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    @Override
    public Optional<Voucher> findById(Integer id) {
        return voucherRepository.findById(id);
    }

    @Override
    @Transactional
    public Voucher save(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        voucherRepository.deleteById(id);
    }
}
