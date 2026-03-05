package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateProductAttributeRequest;
import com.sba302.electroshop.dto.request.UpdateProductAttributeRequest;
import com.sba302.electroshop.dto.response.ProductAttributeResponse;

import java.util.List;

public interface ProductAttributeService {

    ProductAttributeResponse getById(Integer id);

    List<ProductAttributeResponse> getByProduct(Integer productId);

    ProductAttributeResponse create(CreateProductAttributeRequest request);

    ProductAttributeResponse update(Integer id, UpdateProductAttributeRequest request);

    void delete(Integer id);
}
