package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateWarrantyRequest;
import com.sba302.electroshop.dto.response.WarrantyResponse;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.Warranty;
import com.sba302.electroshop.mapper.WarrantyMapper;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.WarrantyRepository;
import com.sba302.electroshop.service.WarrantyService;
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
 class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final ProductRepository productRepository;
    private final WarrantyMapper warrantyMapper;

    @Override
    public WarrantyResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response

        return warrantyRepository.findById(id)
                .map(warrantyMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Warranty not found with id: " + id));
    }

    @Override
    public Page<WarrantyResponse> getByProduct(Integer productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }


        Page<Warranty> warrantyPage =
                warrantyRepository.findByProduct_ProductId(productId, pageable);

        return warrantyPage.map(warrantyMapper::toResponse);
    }


    @Override
    @Transactional
    public WarrantyResponse create(CreateWarrantyRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + request.getProductId())
                );

        Warranty warranty = warrantyMapper.toEntity(request);
        warranty.setProduct(product);

        LocalDateTime startDate =
                request.getStartDate() != null
                        ? request.getStartDate()
                        : LocalDateTime.now();

        warranty.setStartDate(startDate);
        warranty.setEndDate(
                startDate.plusMonths(request.getWarrantyPeriodMonths())
        );

        Warranty saved = warrantyRepository.save(warranty);
        return warrantyMapper.toResponse(saved);
    }


    @Override
    @Transactional
    public WarrantyResponse update(Integer id, CreateWarrantyRequest request) {

        Warranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Warranty not found with id: " + id)
                );

        // update các field thường (có thể bao gồm startDate)
        warrantyMapper.updateEntity(warranty, request);

        // tính lại endDate dựa trên startDate HIỆN TẠI của entity
        if (request.getWarrantyPeriodMonths() != null) {
            LocalDateTime startDate = warranty.getStartDate();

            if (startDate == null) {
                throw new IllegalStateException("Start date must not be null when updating warranty period");
            }

            warranty.setEndDate(
                    startDate.plusMonths(request.getWarrantyPeriodMonths())
            );
        }

        return warrantyMapper.toResponse(warranty);
    }



    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete warranty
        if (!warrantyRepository.existsById(id)) {
            throw new RuntimeException("Warranty not found with id: " + id);
        }
        warrantyRepository.deleteById(id);

    }

    @Override
    public boolean isWarrantyValid(Integer warrantyId) {

        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new RuntimeException("Warranty not found with id: " + warrantyId));

        return warranty.getEndDate() != null
                && warranty.getEndDate().isAfter(LocalDateTime.now());
    }

}
