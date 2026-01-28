package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateWarrantyRequest;
import com.sba302.electroshop.dto.response.WarrantyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarrantyService {

    WarrantyResponse getById(Integer id);

    Page<WarrantyResponse> getByProduct(Integer productId, Pageable pageable);

    WarrantyResponse create(CreateWarrantyRequest request);

    WarrantyResponse update(Integer id, CreateWarrantyRequest request);

    void delete(Integer id);

    boolean isWarrantyValid(Integer warrantyId);
}
