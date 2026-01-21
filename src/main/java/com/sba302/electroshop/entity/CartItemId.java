package com.sba302.electroshop.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartItemId implements Serializable {
    private Integer cart;
    private Integer product;
}
