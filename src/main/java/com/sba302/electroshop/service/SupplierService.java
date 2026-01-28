package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateSupplierRequest;
import com.sba302.electroshop.dto.response.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {

    SupplierResponse getById(Integer id);

    Page<SupplierResponse> search(String keyword, Pageable pageable);

    SupplierResponse create(CreateSupplierRequest request);

    SupplierResponse update(Integer id, CreateSupplierRequest request);

    void delete(Integer id);
}
