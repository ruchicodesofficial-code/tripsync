package com.tripsync.notification.entity;

import com.tripsync.common.entity.BaseEntity;
import com.tripsync.trip.entity.Trip;
import com.tripsync.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications")
public class TripNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "recipient_id",nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "trip_id",nullable = false)
    private Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type",nullable = false,length = 40)
    private NotificationType type;

    @Column(nullable = false,length = 150)
    private String title;

    @Column(nullable = false,length = 1000)
    private String message;

    @Column(name = "is_read",nullable = false)
    private boolean read = false;
}
