package com.sba302.electroshop.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponse {
    private Integer productAttributeId;
    private Integer attributeId;
    private String attributeName;
    private String value;
}
