package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreatePriceTierRequest;
import com.sba302.electroshop.dto.request.UpdatePriceTierRequest;
import com.sba302.electroshop.dto.response.BulkPriceTierResponse;

import java.util.List;

public interface PriceTierService {

    List<BulkPriceTierResponse> getByProductId(Integer productId);

    BulkPriceTierResponse create(CreatePriceTierRequest request);

    BulkPriceTierResponse update(Integer id, UpdatePriceTierRequest request);

    void delete(Integer id);
}
