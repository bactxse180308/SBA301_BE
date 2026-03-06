package com.sba302.electroshop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBulkOrderRequest {

    @NotNull(message = "Company ID is required")
    private Integer companyId;

    @Size(max = 50, message = "Voucher code must not exceed 50 characters")
    private String voucherCode; // optional

    @NotEmpty(message = "Bulk order must have at least one item")
    @Valid
    private List<BulkOrderItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkOrderItemRequest {

        @NotNull(message = "Product ID is required")
        private Integer productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
