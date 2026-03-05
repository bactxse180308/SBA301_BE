package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.VoucherResponse;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.entity.UserVoucher;
import com.sba302.electroshop.entity.Voucher;
import com.sba302.electroshop.enums.VoucherStatus;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.VoucherMapper;
import com.sba302.electroshop.repository.UserVoucherRepository;
import com.sba302.electroshop.repository.VoucherRepository;
import com.sba302.electroshop.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final VoucherMapper voucherMapper;

    @Override
    public VoucherResponse getById(Integer id) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Voucher not found"));

        return voucherMapper.toResponse(voucher);
    }

    @Override
    public VoucherResponse getByCode(String code) {

        Voucher voucher = voucherRepository
                .findByVoucherCode(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Voucher not found"));

        return voucherMapper.toResponse(voucher);
    }

    @Override
    public Page<VoucherResponse> search(String keyword, Boolean validOnly, Pageable pageable) {

        Page<Voucher> page = voucherRepository.findAll(pageable);

        return page.map(voucherMapper::toResponse);
    }

    @Override
    @Transactional
    public VoucherResponse create(CreateVoucherRequest request) {

        Voucher voucher = voucherMapper.toEntity(request);

        voucherRepository.save(voucher);

        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional
    public VoucherResponse update(Integer id, UpdateVoucherRequest request) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Voucher not found"));

        voucherMapper.updateEntity(voucher, request);

        voucherRepository.save(voucher);

        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional
    public void assignToUser(Integer voucherId, Integer userId) {

        UserVoucher uv = new UserVoucher();

        uv.setVoucher(voucherRepository.findById(voucherId).orElseThrow());

        User user = new User();
        user.setUserId(userId);

        uv.setUser(user);
        uv.setStatus(VoucherStatus.AVAILABLE);
        uv.setAssignedAt(LocalDateTime.now());

        userVoucherRepository.save(uv);
    }

    @Override
    public boolean validateVoucher(String code, Integer userId) {

        Voucher voucher = voucherRepository
                .findByVoucherCode(code)
                .orElse(null);

        if (voucher == null) return false;

        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(voucher.getValidFrom())
                && now.isBefore(voucher.getValidTo());
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        voucherRepository.deleteById(id);
    }
}
