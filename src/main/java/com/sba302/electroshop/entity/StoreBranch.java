package com.sba302.electroshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

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
    @Column(name = "branch_id")
    private Integer branchId;

    @Nationalized
    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Nationalized
    @Column(name = "location", length = 500)
    private String location;

    @Nationalized
    @Column(name = "manager_name")
    private String managerName;

    @Nationalized
    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    @Nationalized
    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "working_hours", length = 200)
    private String workingHours;

    @Column(name = "maps_url", length = 1000)
    private String mapsUrl;
}
