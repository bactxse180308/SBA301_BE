package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sba302.electroshop.entity.UserVoucher;
import com.sba302.electroshop.entity.Voucher;

import java.math.BigDecimal;

public interface VoucherService {

    VoucherResponse getById(Integer id);

    VoucherResponse getByCode(String code);

    Page<VoucherResponse> search(String keyword, Boolean validOnly, Pageable pageable);

    Page<VoucherResponse> getVouchersByUserId(Integer userId, Pageable pageable);

    VoucherResponse create(CreateVoucherRequest request);

    VoucherResponse update(Integer id, UpdateVoucherRequest request);

    void assignToUser(Integer voucherId, Integer userId);

    void assignToUsers(Integer voucherId, java.util.List<Integer> userIds);

    boolean validateVoucher(String code, Integer userId);

    UserVoucher validateAndGetVoucher(String code, Integer userId, BigDecimal orderTotal);

    BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal);

    void markVoucherAsUsed(Integer userVoucherId);

    void releaseVoucher(Integer userVoucherId);

    void delete(Integer id);
}
