package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateVoucherRequest;
import com.sba302.electroshop.dto.request.UpdateVoucherRequest;
import com.sba302.electroshop.dto.response.VoucherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {

    VoucherResponse getById(Integer id);

    VoucherResponse getByCode(String code);

    Page<VoucherResponse> search(String keyword, Boolean validOnly, Pageable pageable);

    VoucherResponse create(CreateVoucherRequest request);

    VoucherResponse update(Integer id, UpdateVoucherRequest request);

    void assignToUser(Integer voucherId, Integer userId);

    boolean validateVoucher(String code, Integer userId);

    void delete(Integer id);
}
