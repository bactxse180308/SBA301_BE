package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.VoucherResponse;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.entity.UserVoucher;
import com.sba302.electroshop.entity.Voucher;
import com.sba302.electroshop.enums.DiscountType;
import com.sba302.electroshop.enums.VoucherStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.VoucherMapper;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.repository.UserVoucherRepository;
import com.sba302.electroshop.repository.VoucherRepository;
import com.sba302.electroshop.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepository;

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
    public Page<VoucherResponse> getVouchersByUserId(Integer userId, Pageable pageable) {
        return userVoucherRepository.findByUserId(userId, pageable)
                .map(voucherMapper::toResponse);
    }

    @Override
    @Transactional
    public VoucherResponse create(CreateVoucherRequest request) {

        Voucher voucher = voucherMapper.toEntity(request);
        voucher.setUsedCount(0);
        voucher.setIsActive(true);
        voucher.setIsValid(true);

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
    @Transactional
    public void assignToUsers(Integer voucherId, List<Integer> userIds) {

        // 1. Validate voucher tồn tại và còn active
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + voucherId));

        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            throw new ApiException("Voucher is not active");
        }
        if (!Boolean.TRUE.equals(voucher.getIsValid())) {
            throw new ApiException("Voucher is currently invalid");
        }
        if (voucher.getValidTo() != null && LocalDateTime.now().isAfter(voucher.getValidTo())) {
            throw new ApiException("Voucher đã hết hạn");
        }

        // 2. Validate tất cả userId tồn tại — 1 query thay vì N
        List<Integer> existingUserIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getUserId)
                .toList();

        List<Integer> notFound = userIds.stream()
                .filter(id -> !existingUserIds.contains(id))
                .toList();

        if (!notFound.isEmpty()) {
            throw new ResourceNotFoundException("Users not found: " + notFound);
        }

        // 3. Lọc ra những userId chưa có voucher này — tránh duplicate
        List<Integer> alreadyAssigned = userVoucherRepository
                .findUserIdsByVoucherIdAndUserIdIn(voucherId, userIds); // query bên dưới

        List<Integer> toAssign = userIds.stream()
                .filter(id -> !alreadyAssigned.contains(id))
                .toList();

        if (toAssign.isEmpty()) {
            log.info("All users already have voucherId={}", voucherId);
            return;
        }

        // 4. Build + saveAll — batch insert 1 lần
        LocalDateTime now = LocalDateTime.now();
        List<UserVoucher> userVouchers = toAssign.stream()
                .map(userId -> UserVoucher.builder()
                        .voucher(voucher)
                        .user(userRepository.getReferenceById(userId)) // ✅ proxy, không query
                        .status(VoucherStatus.AVAILABLE)
                        .assignedAt(now)
                        .build())
                .toList();

        userVoucherRepository.saveAll(userVouchers);
        log.info("Assigned voucherId={} to {} users, skipped {} already assigned",
                voucherId, toAssign.size(), alreadyAssigned.size());
    }

    @Override
    public boolean validateVoucher(String code, Integer userId) {
        try {
            validateAndGetVoucher(code, userId, BigDecimal.ZERO);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public UserVoucher validateAndGetVoucher(String code, Integer userId, BigDecimal orderTotal) {
        UserVoucher userVoucher = userVoucherRepository.findByUserIdAndVoucherCode(userId, code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found or not assigned to user: " + code));

        if (userVoucher.getStatus() != VoucherStatus.AVAILABLE) {
            throw new IllegalArgumentException("Voucher is not available for use.");
        }

        Voucher voucher = userVoucher.getVoucher();

        if (Boolean.FALSE.equals(voucher.getIsActive())) {
            throw new IllegalArgumentException("Voucher is inactive.");
        }

        if (Boolean.FALSE.equals(voucher.getIsValid())) {
            throw new IllegalArgumentException("Voucher is currently invalid.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (voucher.getValidFrom() != null && now.isBefore(voucher.getValidFrom())) {
            throw new IllegalArgumentException("Voucher is not yet valid.");
        }

        if (voucher.getValidTo() != null && now.isAfter(voucher.getValidTo())) {
            throw new IllegalArgumentException("Voucher has expired.");
        }

        if (voucher.getUsageLimit() != null && voucher.getUsedCount() != null
                && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new IllegalArgumentException("Voucher usage limit reached.");
        }

        if (voucher.getMinOrderValue() != null && orderTotal != null
                && orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Order total does not meet the minimum requirement for this voucher.");
        }

        return userVoucher;
    }

    @Override
    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal) {
        if (voucher == null || orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (voucher.getDiscountType() == DiscountType.PERCENT) {
            if (voucher.getDiscountValue() != null) {
                discount = orderTotal.multiply(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100));
            }
            if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                discount = voucher.getMaxDiscount();
            }
        } else if (voucher.getDiscountType() == DiscountType.FIXED) {
            discount = voucher.getDiscountValue() != null ? voucher.getDiscountValue() : BigDecimal.ZERO;
        }

        // Return minimum of discount and order total to prevent negative final amounts
        return discount.compareTo(orderTotal) > 0 ? orderTotal : discount;
    }

    @Override
    @Transactional
    public void markVoucherAsUsed(Integer userVoucherId) {
        UserVoucher userVoucher = userVoucherRepository.findById(userVoucherId)
                .orElseThrow(() -> new IllegalArgumentException("UserVoucher not found."));

        if (userVoucher.getStatus() != VoucherStatus.AVAILABLE) {
            throw new IllegalArgumentException("Voucher is not available to be marked as used.");
        }

        userVoucher.setStatus(VoucherStatus.USED);
        userVoucher.setUsedAt(LocalDateTime.now());
        userVoucherRepository.save(userVoucher);

        Voucher voucher = userVoucher.getVoucher();
        if (voucher.getUsedCount() == null) {
             voucher.setUsedCount(1);
        } else {
             voucher.setUsedCount(voucher.getUsedCount() + 1);
        }
        voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void releaseVoucher(Integer userVoucherId) {
        UserVoucher userVoucher = userVoucherRepository.findById(userVoucherId)
                .orElseThrow(() -> new IllegalArgumentException("UserVoucher not found."));

        if (userVoucher.getStatus() != VoucherStatus.USED) {
            log.warn("Voucher is not in USED status, status={}", userVoucher.getStatus());
            return;
        }

        userVoucher.setStatus(VoucherStatus.AVAILABLE);
        userVoucher.setUsedAt(null);
        userVoucherRepository.save(userVoucher);

        Voucher voucher = userVoucher.getVoucher();
        if (voucher.getUsedCount() != null && voucher.getUsedCount() > 0) {
            voucher.setUsedCount(voucher.getUsedCount() - 1);
            voucherRepository.save(voucher);
        }
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        voucherRepository.deleteById(id);
    }
}
