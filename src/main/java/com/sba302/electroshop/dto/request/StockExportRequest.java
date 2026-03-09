package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StockExportRequest {
    @NotNull(message = "Branch ID is required")
    private Integer branchId;

    private String note;

    private LocalDateTime createdDate;

    @NotEmpty(message = "Export items cannot be empty")
    private List<StockExportLineRequest> items;
}
