package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.PublicSearchResponse;
import com.sba302.electroshop.enums.ProductStatus;
import com.sba302.electroshop.repository.BrandRepository;
import com.sba302.electroshop.repository.CategoryRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.VoucherRepository;
import com.sba302.electroshop.service.PublicSearchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicSearchServiceImpl implements PublicSearchService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final VoucherRepository voucherRepository;

    private final PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setReadOnly(true);
    }

    @Override
    public PublicSearchResponse search(String keyword, int limit) {
        log.info("Public search with keyword='{}', limit={}", keyword, limit);
        Pageable pageable = PageRequest.of(0, limit);
        String searchKeyword = "%" + keyword.trim().toLowerCase() + "%";

        CompletableFuture<List<PublicSearchResponse.ProductDto>> productsFuture = CompletableFuture.supplyAsync(() ->
                searchProducts(searchKeyword, pageable));
        CompletableFuture<List<PublicSearchResponse.CategoryDto>> categoriesFuture = CompletableFuture.supplyAsync(() ->
                searchCategories(searchKeyword, pageable));
        CompletableFuture<List<PublicSearchResponse.BrandDto>> brandsFuture = CompletableFuture.supplyAsync(() ->
                searchBrands(searchKeyword, pageable));
        CompletableFuture<List<PublicSearchResponse.VoucherDto>> vouchersFuture = CompletableFuture.supplyAsync(() ->
                searchVouchers(searchKeyword, pageable));

        CompletableFuture.allOf(productsFuture, categoriesFuture, brandsFuture, vouchersFuture).join();

        return PublicSearchResponse.builder()
                .products(productsFuture.join())
                .categories(categoriesFuture.join())
                .brands(brandsFuture.join())
                .vouchers(vouchersFuture.join())
                .build();
    }

    private List<PublicSearchResponse.ProductDto> searchProducts(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return productRepository.searchByKeyword(keyword, pageable).stream()
                        .filter(p -> ProductStatus.AVAILABLE.equals(p.getStatus()))
                        .map(p -> PublicSearchResponse.ProductDto.builder()
                                .productId(p.getProductId())
                                .productName(p.getProductName())
                                .mainImage(p.getMainImage())
                                .price(p.getPrice())
                                .discountPercent(p.getDiscountPercent())
                                .rating(p.getRating())
                                .categoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null)
                                .brandName(p.getBrand() != null ? p.getBrand().getBrandName() : null)
                                .build())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching products: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<PublicSearchResponse.CategoryDto> searchCategories(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return categoryRepository.searchByKeyword(keyword, pageable).stream()
                        .map(c -> PublicSearchResponse.CategoryDto.builder()
                                .categoryId(c.getCategoryId())
                                .categoryName(c.getCategoryName())
                                .build())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching categories: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<PublicSearchResponse.BrandDto> searchBrands(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return brandRepository.searchByKeyword(keyword, pageable).stream()
                        .map(b -> PublicSearchResponse.BrandDto.builder()
                                .brandId(b.getBrandId())
                                .brandName(b.getBrandName())
                                .build())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching brands: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<PublicSearchResponse.VoucherDto> searchVouchers(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return voucherRepository.searchByKeyword(keyword, pageable).stream()
                        .filter(v -> Boolean.TRUE.equals(v.getIsActive()) &&
                                (v.getValidTo() == null || v.getValidTo().isAfter(LocalDateTime.now())))
                        .map(v -> PublicSearchResponse.VoucherDto.builder()
                                .voucherCode(v.getVoucherCode())
                                .description(v.getDescription())
                                .discountPercent(v.getDiscountValue())
                                .build())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching vouchers: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }
}
