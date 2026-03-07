package com.sba302.electroshop.entity;

import com.sba302.electroshop.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Nationalized
    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Nationalized
    @Column(name = "address", length = 500)
    private String address;

    @Nationalized
    @Column(name = "representative_name", length = 255)
    private String representativeName; // Tên người đại diện pháp luật

    @Nationalized
    @Column(name = "representative_position", length = 100)
    private String representativePosition; // Chức vụ người đại diện (VD: Giám đốc, CEO)

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "founding_date")
    private LocalDate foundingDate; // Ngày thành lập

    @Nationalized
    @Column(name = "business_type", length = 100)
    private String businessType; // Loại hình doanh nghiệp (TNHH, Cổ phần, v.v.)

    @Column(name = "employee_count")
    private Integer employeeCount; // Quy mô nhân sự

    @Nationalized
    @Column(name = "industry", length = 255)
    private String industry; // Lĩnh vực kinh doanh

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private CompanyStatus status; // Trạng thái phê duyệt công ty

    @Column(name = "logo_url", length = 500)
    private String logoUrl; // Đường dẫn logo công ty

    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // Thời điểm được admin phê duyệt
}