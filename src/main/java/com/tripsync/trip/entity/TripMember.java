package com.tripsync.trip.entity;

import com.tripsync.common.entity.BaseEntity;
import com.tripsync.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "trip_members",uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_trip_members_trip_user",
                columnNames = {"trip_id","user_id"}
        )
})
public class TripMember extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY,optional = false)
        @JoinColumn(name="trip_id",nullable = false)
        private Trip trip;

        @ManyToOne(fetch = FetchType.LAZY,optional = false)
        @JoinColumn(name="user_id",nullable = false)
        private User user;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false,length = 20)
        private TripMemberRole role=TripMemberRole.VIEWER;

        @Column(name = "joined_at",nullable = false)
        private LocalDateTime joinedAt;
}
