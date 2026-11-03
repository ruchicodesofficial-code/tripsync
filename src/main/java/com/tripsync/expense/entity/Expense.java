package com.tripsync.expense.entity;

import com.tripsync.common.entity.BaseEntity;
import com.tripsync.trip.entity.Trip;
import com.tripsync.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="expenses")
public class Expense extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="trip_id",nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="paid_by_id",nullable = false)
    private User paidBy;

    @Column(nullable = false,length = 150)
    private String title;

    @Column(length = 1000)
    private String  description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private ExpenseCategory category;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

}
