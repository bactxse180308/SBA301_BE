package com.sba302.electroshop.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeResponse {
    private Integer attributeId;
    private String attributeName;
}
