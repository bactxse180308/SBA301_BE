package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "SUPPLIER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Integer supplierId;

    @Nationalized
    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Nationalized
    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Nationalized
    @Column(name = "address", length = 500)
    private String address;
}
