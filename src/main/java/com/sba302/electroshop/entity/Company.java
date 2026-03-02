package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "COMPANY")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;
}