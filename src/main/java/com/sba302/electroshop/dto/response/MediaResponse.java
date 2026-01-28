package com.sba302.electroshop.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    private Integer mediaId;
    private Integer productId;
    private String type;
    private String url;
    private Integer sortOrder;
}
