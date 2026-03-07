package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpSendRequest {
    @NotBlank
    @Email
    private String email;
}
