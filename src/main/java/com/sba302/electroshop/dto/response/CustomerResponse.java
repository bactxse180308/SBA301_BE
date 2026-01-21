package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CustomerResponse {
    private Integer customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDateTime registrationDate;
    private String status;
    private Integer rewardPoint;
}
