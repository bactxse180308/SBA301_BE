package com.sba302.electroshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreBranchRequest {

    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 255, message = "Branch name must be between 2 and 255 characters")
    private String branchName;

    @Size(max = 500, message = "Location must not exceed 500 characters")
    private String location;

    @Size(max = 255, message = "Manager name must not exceed 255 characters")
    private String managerName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    private String contactNumber;
}
