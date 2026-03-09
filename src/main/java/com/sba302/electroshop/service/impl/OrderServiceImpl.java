package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateOrderRequest;
import com.sba302.electroshop.dto.response.OrderResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.enums.ProductStatus;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.OrderMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.OrderService;
import com.sba302.electroshop.service.VoucherService;
import com.sba302.electroshop.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final OrderMapper orderMapper;
    private final VoucherService voucherService;

    // ================================================================
    // PUBLIC METHODS
    // ================================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> search(Integer userId, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = OrderSpecification.filterOrders(userId, status);
        Page<Order> page = orderRepository.findAll(spec, pageable);
        return page.map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Integer userId, CreateOrderRequest request) {
        log.info("Placing order for userId={}", userId);

        // 1. Validate user active
        User user = findActiveUser(userId);

        // 2. Validate items — check duplicate productId
        validateNoDuplicateProducts(request.getItems());

        // 3. Batch fetch products, branches + validate (tồn tại, status, price, stock)
        OrderValidationContext validationContext = fetchAndValidateProducts(request.getItems());

        // 4. Build order (chưa save — chưa có totalAmount)
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        // ✅ FIX: save 1 lần để lấy orderId cho FK trong OrderDetail
        order = orderRepository.save(order);

        // 5. Build details + deduct stock (batch) → trả về totalAmount gốc
        BigDecimal totalAmount = buildOrderDetailsAndDeductStock(order, request.getItems(), validationContext);

        // 6. Apply voucher nếu có
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            UserVoucher userVoucher = voucherService.validateAndGetVoucher(
                    request.getVoucherCode(), userId, totalAmount);

            BigDecimal discount = voucherService.calculateDiscount(
                    userVoucher.getVoucher(), totalAmount);

            BigDecimal finalAmount = totalAmount.subtract(discount).max(BigDecimal.ZERO);

            order.setTotalAmount(totalAmount);       // ✅ tổng gốc trước giảm
            order.setDiscountAmount(discount);        // ✅ snapshot discount
            order.setFinalAmount(finalAmount);        // ✅ thực tế thanh toán
            order.setUserVoucher(userVoucher);

            voucherService.markVoucherAsUsed(userVoucher.getUserVoucherId());
            Order savedOrder = orderRepository.save(order);

            log.info("Order placed with voucher, id={}, discount={}", savedOrder.getOrderId(), discount);
            return orderMapper.toResponse(savedOrder);
        }

        // 7. Không có voucher — save thẳng
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with id={}", savedOrder.getOrderId());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Integer orderId, OrderStatus newStatus) {
        log.info("Updating order status: orderId={}, newStatus={}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {
        log.info("Cancelling order: orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ================================================================
    // PRIVATE HELPER METHODS
    // ================================================================

    /**
     * Validate user exists and is ACTIVE.
     */
    private User findActiveUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException("User account is not active. Current status: " + user.getStatus());
        }
        return user;
    }

    /**
     * Check for duplicate productIds in the request.
     */
    private void validateNoDuplicateProducts(List<CreateOrderRequest.OrderItemRequest> items) {
        Set<Integer> uniqueIds = new HashSet<>();
        for (var item : items) {
            if (!uniqueIds.add(item.getProductId())) {
                throw new ApiException("Duplicate product ID in order items: " + item.getProductId());
            }
        }
    }

    private record OrderValidationContext(
            Map<Integer, Product> productMap,
            Map<Integer, BranchProductStock> selectedStockMap // productId -> selected BranchProductStock
    ) {}

    /**
     * Batch fetch all products and their available stocks, then for each item find a branch with sufficient stock.
     */
    private OrderValidationContext fetchAndValidateProducts(List<CreateOrderRequest.OrderItemRequest> items) {
        List<Integer> productIds = items.stream()
                .map(CreateOrderRequest.OrderItemRequest::getProductId)
                .distinct()
                .toList();

        // 1 query for Products
        Map<Integer, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        // 1 query for ALL available stocks for these products
        List<BranchProductStock> allStocks = branchProductStockRepository.findAllByProductIds(productIds);

        // Group stocks by productId
        Map<Integer, List<BranchProductStock>> stocksByProduct = allStocks.stream()
                .collect(Collectors.groupingBy(bps -> bps.getProduct().getProductId()));

        Map<Integer, BranchProductStock> selectedStockMap = new HashMap<>();

        for (var item : items) {
            Product product = productMap.get(item.getProductId());
            validateProductBasics(product, item.getProductId());

            // Find best branch for this product
            List<BranchProductStock> availableStocks = stocksByProduct.getOrDefault(item.getProductId(), Collections.emptyList());

            BranchProductStock selectedStock = availableStocks.stream()
                    .filter(stock -> stock.getQuantity() >= item.getQuantity())
                    .max(Comparator.comparingInt(BranchProductStock::getQuantity)) // Pick branch with most stock
                    .orElseThrow(() -> new ApiException("Insufficient stock for product: " + product.getProductName()
                            + ". No branch has " + item.getQuantity() + " units available."));

            selectedStockMap.put(item.getProductId(), selectedStock);
        }

        return new OrderValidationContext(productMap, selectedStockMap);
    }

    private void validateProductBasics(Product product, Integer productId) {
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new ApiException("Product is not available: " + product.getProductName()
                    + " (status: " + product.getStatus() + ")");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Product price is invalid: " + product.getProductName());
        }
    }

    /**
     * Build OrderDetail list, deduct stock (main + branch), and saveAll in batch.
     * Returns the original total amount before any discount.
     */
    private BigDecimal buildOrderDetailsAndDeductStock(Order order,
                                                       List<CreateOrderRequest.OrderItemRequest> items,
                                                       OrderValidationContext validationContext) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();
        List<BranchProductStock> updatedBranchStocks = new ArrayList<>();

        for (var item : items) {
            Product product = validationContext.productMap().get(item.getProductId());
            BranchProductStock branchStock = validationContext.selectedStockMap().get(item.getProductId());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .branch(branchStock.getBranch())
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            branchStock.setQuantity(branchStock.getQuantity() - item.getQuantity());
            branchStock.setLastUpdated(LocalDateTime.now());
            updatedBranchStocks.add(branchStock);

            details.add(detail);
            total = total.add(subtotal);
        }

        // Batch insert / update
        orderDetailRepository.saveAll(details);
        if (!updatedBranchStocks.isEmpty()) {
            branchProductStockRepository.saveAll(updatedBranchStocks);
        }

        return total;
    }
}