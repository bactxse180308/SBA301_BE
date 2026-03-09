package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateProductRequest;
import com.sba302.electroshop.dto.request.UpdateProductRequest;
import com.sba302.electroshop.dto.response.ProductResponse;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.ProductMapper;
import com.sba302.electroshop.repository.BrandRepository;
import com.sba302.electroshop.repository.CategoryRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.BranchProductStockRepository;
import com.sba302.electroshop.repository.SupplierRepository;
import com.sba302.electroshop.service.ProductService;
import com.sba302.electroshop.service.StoreBranchService;
import com.sba302.electroshop.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final ProductMapper productMapper;
    private final StoreBranchService storeBranchService;

    @Override
    public ProductResponse getById(Integer id) {
        log.info("Fetching product with id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        ProductResponse response = productMapper.toResponse(product);
        response.setQuantity(branchProductStockRepository.sumQuantityByProductId(id));
        return response;
    }

    @Override
    public Page<ProductResponse> search(String keyword, Integer categoryId, Integer brandId, Pageable pageable) {
        log.info("Searching products keyword={}, categoryId={}, brandId={}", keyword, categoryId, brandId);
        Specification<Product> spec = ProductSpecification.filter(keyword, categoryId, brandId);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        
        List<Integer> productIds = productPage.getContent().stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());
        
        Map<Integer, Integer> stockMap = branchProductStockRepository.sumQuantityByProductIds(productIds).stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
        
        return productPage.map(product -> {
            ProductResponse response = productMapper.toResponse(product);
            response.setQuantity(stockMap.getOrDefault(product.getProductId(), 0));
            return response;
        });
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        log.info("Creating product: {}", request.getProductName());

        Product product = productMapper.toEntity(request);
        product.setCreatedDate(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId())));
        }

        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId())));
        }

        if (request.getSupplierId() != null) {
            product.setSupplier(supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId())));
        }

        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Integer id, UpdateProductRequest request) {
        log.info("Updating product id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productMapper.updateEntity(product, request);

        if (request.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId())));
        }

        if (request.getBrandId() != null) {
            product.setBrand(brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId())));
        }

        if (request.getSupplierId() != null) {
            product.setSupplier(supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId())));
        }

        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting product id={}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

}
