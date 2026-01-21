package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reward_point")
    private Integer rewardPoint;
}
