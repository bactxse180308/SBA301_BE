package com.sba302.electroshop.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WishlistItemId implements Serializable {
    private Integer wishlist;
    private Integer product;
}
