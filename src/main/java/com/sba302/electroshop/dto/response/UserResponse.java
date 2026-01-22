package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private Integer userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String role;
    private Boolean isActive;
    private LocalDateTime registrationDate;
    private String status;
    private Integer rewardPoint;
}
