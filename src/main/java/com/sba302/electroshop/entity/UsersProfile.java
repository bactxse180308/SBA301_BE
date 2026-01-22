package com.sba302.electroshop.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USERS_PROFILE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersProfile {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // Personal fields (for CUSTOMER)
    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "reward_point")
    private Integer rewardPoint;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // Company fields (for COMPANY)
    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "tax_code", length = 50)
    private String taxCode;
}
