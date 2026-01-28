package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateProductRequest;
import com.sba302.electroshop.dto.request.UpdateProductRequest;
import com.sba302.electroshop.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    // Query operations (combined search with optional filters)
    ProductResponse getById(Integer id);

    Page<ProductResponse> search(String keyword, Integer categoryId, Integer brandId, Pageable pageable);

    // Command operations
    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Integer id, UpdateProductRequest request);

    void delete(Integer id);

    void updateStock(Integer id, Integer quantity);
}
