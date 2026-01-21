package com.sba302.electroshop.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerVoucherId implements Serializable {
    private Integer customer;
    private Integer voucher;
}
