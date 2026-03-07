package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2Request {
    @NotBlank(message = "OAuth2 token cannot be blank")
    private String token;
}
