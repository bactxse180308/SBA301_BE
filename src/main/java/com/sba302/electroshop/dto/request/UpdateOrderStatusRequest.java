package com.sba302.electroshop.dto.request;

import com.sba302.electroshop.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
