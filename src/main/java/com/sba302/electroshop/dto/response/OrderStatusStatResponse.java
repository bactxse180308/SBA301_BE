package com.sba302.electroshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusStatResponse {
    private String name; // e.g., "Pending", "Delivered"
    private Long value;
}
