package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateProductAttributeRequest;
import com.sba302.electroshop.dto.request.UpdateProductAttributeRequest;
import com.sba302.electroshop.dto.response.ProductAttributeResponse;
import com.sba302.electroshop.entity.Attribute;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.ProductAttribute;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.ProductAttributeMapper;
import com.sba302.electroshop.repository.AttributeRepository;
import com.sba302.electroshop.repository.ProductAttributeRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final ProductAttributeMapper productAttributeMapper;

    @Override
    public ProductAttributeResponse getById(Integer id) {
        log.info("Fetching product attribute with id={}", id);
        return productAttributeRepository.findById(id)
                .map(productAttributeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product attribute not found with id: " + id));
    }

    @Override
    public List<ProductAttributeResponse> getByProduct(Integer productId) {
        log.info("Fetching attributes for product id={}", productId);
        return productAttributeRepository.findByProduct_ProductId(productId).stream()
                .map(productAttributeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductAttributeResponse create(CreateProductAttributeRequest request) {
        log.info("Creating attribute for product id={}", request.getProductId());
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
        
        Attribute attribute = attributeRepository.findById(request.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + request.getAttributeId()));

        ProductAttribute productAttribute = productAttributeMapper.toEntity(request);
        productAttribute.setProduct(product);
        productAttribute.setAttribute(attribute);
        
        productAttribute = productAttributeRepository.save(productAttribute);
        return productAttributeMapper.toResponse(productAttribute);
    }

    @Override
    @Transactional
    public ProductAttributeResponse update(Integer id, UpdateProductAttributeRequest request) {
        log.info("Updating product attribute id={}", id);
        
        ProductAttribute productAttribute = productAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product attribute not found with id: " + id));

        productAttributeMapper.updateEntity(productAttribute, request);
        
        productAttribute = productAttributeRepository.save(productAttribute);
        return productAttributeMapper.toResponse(productAttribute);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting product attribute id={}", id);
        if (!productAttributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product attribute not found with id: " + id);
        }
        productAttributeRepository.deleteById(id);
    }
}
