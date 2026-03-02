package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateBulkOrderRequest;
import com.sba302.electroshop.dto.request.CreateCustomizationRequest;
import com.sba302.electroshop.dto.response.BulkOrderResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.BulkOrderStatus;
import com.sba302.electroshop.enums.CustomizationStatus;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.BulkOrderMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.BulkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class BulkOrderServiceImpl implements BulkOrderService {

    private final BulkOrderRepository bulkOrderRepository;
    private final BulkOrderDetailRepository bulkOrderDetailRepository;
    private final OrderCustomizationRepository orderCustomizationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final BulkOrderMapper bulkOrderMapper;

    @Override
    public BulkOrderResponse getById(Integer id) {
        log.info("Fetching bulk order with id: {}", id);
        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));
        return bulkOrderMapper.toResponse(bulkOrder);
    }

    @Override
    public Page<BulkOrderResponse> search(Integer userId, BulkOrderStatus status, Pageable pageable) {
        log.info("Searching bulk orders with userId: {}, status: {}", userId, status);
        Page<BulkOrder> bulkOrders;

        if (userId != null && status != null) {
            bulkOrders = bulkOrderRepository.findByUserUserIdAndStatus(userId, status, pageable);
        } else if (userId != null) {
            bulkOrders = bulkOrderRepository.findByUserUserId(userId, pageable);
        } else if (status != null) {
            bulkOrders = bulkOrderRepository.findByStatus(status, pageable);
        } else {
            bulkOrders = bulkOrderRepository.findAll(pageable);
        }

        return bulkOrders.map(bulkOrderMapper::toResponse);
    }

    @Override
    @Transactional
    public BulkOrderResponse create(Integer userId, CreateBulkOrderRequest request) {
        log.info("Creating bulk order for user: {}, company: {}", userId, request.getCompanyId());

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate company exists
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        // Create bulk order
        BulkOrder bulkOrder = BulkOrder.builder()
                .user(user)
                .company(company)
                .createdAt(LocalDateTime.now())
                .status(BulkOrderStatus.PENDING)
                .build();

        BulkOrder savedBulkOrder = bulkOrderRepository.save(bulkOrder);

        // Create bulk order details
        List<BulkOrderDetail> details = request.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

                    return BulkOrderDetail.builder()
                            .bulkOrder(savedBulkOrder)
                            .product(product)
                            .quantity(item.getQuantity())
                            .unitPriceSnapshot(product.getPrice())
                            .discountSnapshot(BigDecimal.ZERO)
                            .build();
                })
                .toList();

        bulkOrderDetailRepository.saveAll(details);

        log.info("Bulk order created successfully with id: {}", savedBulkOrder.getBulkOrderId());
        return bulkOrderMapper.toResponse(savedBulkOrder);
    }

    @Override
    @Transactional
    public BulkOrderResponse updateStatus(Integer id, BulkOrderStatus status) {
        log.info("Updating bulk order status with id: {}, new status: {}", id, status);

        BulkOrder bulkOrder = bulkOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order not found with id: " + id));

        bulkOrder.setStatus(status);
        BulkOrder updatedBulkOrder = bulkOrderRepository.save(bulkOrder);

        log.info("Bulk order status updated successfully");
        return bulkOrderMapper.toResponse(updatedBulkOrder);
    }

    @Override
    @Transactional
    public BulkOrderResponse addCustomization(Integer bulkOrderDetailId, CreateCustomizationRequest request) {
        log.info("Adding customization to bulk order detail: {}", bulkOrderDetailId);

        BulkOrderDetail detail = bulkOrderDetailRepository.findById(bulkOrderDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("Bulk order detail not found with id: " + bulkOrderDetailId));

        OrderCustomization customization = OrderCustomization.builder()
                .bulkOrderDetail(detail)
                .type(request.getType())
                .note(request.getNote())
                .extraFee(request.getExtraFee())
                .status(CustomizationStatus.PENDING)
                .build();

        orderCustomizationRepository.save(customization);

        log.info("Customization added successfully");
        return bulkOrderMapper.toResponse(detail.getBulkOrder());
    }
}
