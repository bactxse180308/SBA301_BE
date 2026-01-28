package com.sba302.electroshop.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreBranchResponse {
    private Integer branchId;
    private String branchName;
    private String location;
    private String managerName;
    private String contactNumber;
}
