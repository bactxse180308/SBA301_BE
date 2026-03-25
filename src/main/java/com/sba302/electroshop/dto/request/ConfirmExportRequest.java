package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfirmExportRequest {

    @NotNull(message = "Branch ID is required")
    private Integer branchId;

    @NotEmpty(message = "Export items cannot be empty")
    private List<ExportItem> items;

    @Getter
    @Setter
    public static class ExportItem {
        @NotNull(message = "Product ID is required")
        private Integer productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be greater than 0")
        private Integer quantity;
    }
}
