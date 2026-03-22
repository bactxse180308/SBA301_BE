package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.UpdateCustomerWarrantyRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.CustomerWarrantyResponse;
import com.sba302.electroshop.service.CustomerWarrantyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warranties/customer")
@RequiredArgsConstructor
@Tag(name = "Customer Warranty", description = "APIs quản lý bảo hành của khách hàng")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerWarrantyController {

    private final CustomerWarrantyService customerWarrantyService;

    // ----------------------------------------------------------------
    // User xem bảo hành của chính mình
    // ----------------------------------------------------------------

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Xem tất cả bảo hành của tôi",
               description = "Trả về danh sách bảo hành thuộc user đang đăng nhập, sắp xếp theo ngày hết hạn")
    public ApiResponse<List<CustomerWarrantyResponse>> getMyWarranties() {
        Integer userId = getCurrentUserId();
        return ApiResponse.success(customerWarrantyService.getMyWarranties(userId));
    }

    @GetMapping("/my/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Xem bảo hành còn hiệu lực của tôi",
               description = "Chỉ trả về bảo hành có status ACTIVE và chưa quá ngày hết hạn")
    public ApiResponse<List<CustomerWarrantyResponse>> getMyActiveWarranties() {
        Integer userId = getCurrentUserId();
        return ApiResponse.success(customerWarrantyService.getMyActiveWarranties(userId));
    }

    // ----------------------------------------------------------------
    // Tra cứu theo đơn hàng
    // ----------------------------------------------------------------

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    @Operation(summary = "Tra cứu bảo hành theo đơn hàng thường",
               description = "ADMIN hoặc user có thể tra cứu bảo hành theo orderId")
    public ApiResponse<List<CustomerWarrantyResponse>> getByOrderId(@PathVariable Integer orderId) {
        return ApiResponse.success(customerWarrantyService.getByOrderId(orderId));
    }

    @GetMapping("/bulk-order/{bulkOrderId}")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    @Operation(summary = "Tra cứu bảo hành theo đơn hàng B2B",
               description = "ADMIN hoặc user có thể tra cứu bảo hành theo bulkOrderId")
    public ApiResponse<List<CustomerWarrantyResponse>> getByBulkOrderId(@PathVariable Integer bulkOrderId) {
        return ApiResponse.success(customerWarrantyService.getByBulkOrderId(bulkOrderId));
    }

    // ----------------------------------------------------------------
    // Admin xem/quản lý bảo hành của user bất kỳ
    // ----------------------------------------------------------------

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Xem bảo hành của user bất kỳ",
               description = "Chỉ ADMIN mới được xem bảo hành của bất kỳ user nào theo userId")
    public ApiResponse<List<CustomerWarrantyResponse>> getByUserId(@PathVariable Integer userId) {
        return ApiResponse.success(customerWarrantyService.getByUserId(userId));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Cập nhật notes/status của bảo hành",
               description = "ADMIN có thể chỉnh sửa notes và/hoặc status của một CustomerWarranty")
    public ApiResponse<CustomerWarrantyResponse> update(
            @PathVariable Integer id,
            @RequestBody UpdateCustomerWarrantyRequest request) {
        return ApiResponse.success(
                customerWarrantyService.update(id, request.getNotes(), request.getStatus()));
    }

    // ----------------------------------------------------------------
    // Private helper
    // ----------------------------------------------------------------

    /**
     * Lấy userId từ SecurityContext (principal được set là userId string trong JwtAuthFilter).
     */
    private Integer getCurrentUserId() {
        String principal = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return Integer.parseInt(principal);
    }
}
