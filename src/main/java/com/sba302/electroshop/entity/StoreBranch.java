package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "STORE_BRANCH")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName;

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;
}
