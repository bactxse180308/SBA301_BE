package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.*;
import com.sba302.electroshop.mapper.*;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final VoucherRepository voucherRepository;
    private final StoreBranchRepository storeBranchRepository;

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;
    private final SupplierMapper supplierMapper;
    private final VoucherMapper voucherMapper;
    private final StoreBranchMapper storeBranchMapper;

    private final PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setReadOnly(true);
    }

    @Override
    public GlobalSearchResponse globalSearch(String keyword, int limit) {
        log.info("Global async search with keyword='{}', limit={}", keyword, limit);

        Pageable pageable = PageRequest.of(0, limit);
        String searchKeyword = "%" + keyword.trim().toLowerCase() + "%";

        CompletableFuture<List<ProductResponse>> productsFuture = CompletableFuture.supplyAsync(() ->
                searchProducts(searchKeyword, pageable));
        CompletableFuture<List<OrderResponse>> ordersFuture = CompletableFuture.supplyAsync(() ->
                searchOrders(searchKeyword, pageable));
        CompletableFuture<List<UserResponse>> customersFuture = CompletableFuture.supplyAsync(() ->
                searchCustomers(searchKeyword, pageable));
        CompletableFuture<List<ReviewResponse>> reviewsFuture = CompletableFuture.supplyAsync(() ->
                searchReviews(searchKeyword, pageable));
        CompletableFuture<List<CategoryResponse>> categoriesFuture = CompletableFuture.supplyAsync(() ->
                searchCategories(searchKeyword, pageable));
        CompletableFuture<List<BrandResponse>> brandsFuture = CompletableFuture.supplyAsync(() ->
                searchBrands(searchKeyword, pageable));
        CompletableFuture<List<SupplierResponse>> suppliersFuture = CompletableFuture.supplyAsync(() ->
                searchSuppliers(searchKeyword, pageable));
        CompletableFuture<List<VoucherResponse>> vouchersFuture = CompletableFuture.supplyAsync(() ->
                searchVouchers(searchKeyword, pageable));
        CompletableFuture<List<StoreBranchResponse>> storeBranchesFuture = CompletableFuture.supplyAsync(() ->
                searchStoreBranches(searchKeyword, pageable));

        CompletableFuture.allOf(productsFuture, ordersFuture, customersFuture, reviewsFuture,
                categoriesFuture, brandsFuture, suppliersFuture, vouchersFuture, storeBranchesFuture).join();

        return GlobalSearchResponse.builder()
                .products(productsFuture.join())
                .orders(ordersFuture.join())
                .customers(customersFuture.join())
                .reviews(reviewsFuture.join())
                .categories(categoriesFuture.join())
                .brands(brandsFuture.join())
                .suppliers(suppliersFuture.join())
                .vouchers(vouchersFuture.join())
                .storeBranches(storeBranchesFuture.join())
                .build();
    }

    private List<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return productRepository.searchByKeyword(keyword, pageable).stream()
                        .map(productMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching products: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<OrderResponse> searchOrders(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return orderRepository.searchByKeyword(keyword, pageable).stream()
                        .map(orderMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching orders: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<UserResponse> searchCustomers(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return userRepository.searchCustomersByKeyword(keyword, pageable).stream()
                        .map(userMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching customers: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<ReviewResponse> searchReviews(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return reviewRepository.searchByKeyword(keyword, pageable).stream()
                        .map(reviewMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching reviews: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<CategoryResponse> searchCategories(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return categoryRepository.searchByKeyword(keyword, pageable).stream()
                        .map(categoryMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching categories: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<BrandResponse> searchBrands(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return brandRepository.searchByKeyword(keyword, pageable).stream()
                        .map(brandMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching brands: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<SupplierResponse> searchSuppliers(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return supplierRepository.searchByKeyword(keyword, pageable).stream()
                        .map(supplierMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching suppliers: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<VoucherResponse> searchVouchers(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return voucherRepository.searchByKeyword(keyword, pageable).stream()
                        .map(voucherMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching vouchers: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }

    private List<StoreBranchResponse> searchStoreBranches(String keyword, Pageable pageable) {
        return transactionTemplate.execute(status -> {
            try {
                return storeBranchRepository.searchByKeyword(keyword, pageable).stream()
                        .map(storeBranchMapper::toResponse)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching store branches: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        });
    }
}
