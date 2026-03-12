package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GlobalSearchResponse {
    private List<ProductResponse> products;
    private List<OrderResponse> orders;
    private List<UserResponse> customers;
    private List<ReviewResponse> reviews;
    private List<CategoryResponse> categories;
    private List<BrandResponse> brands;
    private List<SupplierResponse> suppliers;
    private List<VoucherResponse> vouchers;
    private List<StoreBranchResponse> storeBranches;
}
