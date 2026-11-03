package com.tripsync.settlement.entity;


import com.tripsync.common.entity.BaseEntity;
import com.tripsync.trip.entity.Trip;
import com.tripsync.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="settlements")
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "trip_id",nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "payer_id",nullable = false)
    private User payer;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "payee_id",nullable = false)
    private User payee;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal amount;

    @Column(name = "settled_at",nullable = false)
    private LocalDateTime settleAt;

    @Column(length = 500)
    private String note;
}
