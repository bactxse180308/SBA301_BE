package com.sba302.electroshop.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BranchProductStockId implements Serializable {
    private Integer warehouse;
    private Integer product;
}
