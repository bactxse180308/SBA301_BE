package com.sba302.electroshop.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "REVIEW")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "rating")
    private Integer rating;

    @Nationalized
    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Nationalized
    @Column(name = "reply_comment", length = 2000)
    private String replyComment;

    @Column(name = "reply_date")
    private LocalDateTime replyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replied_by")
    private User repliedBy;
}
