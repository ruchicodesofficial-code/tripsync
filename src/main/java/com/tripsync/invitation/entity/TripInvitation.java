package com.tripsync.invitation.entity;

import com.tripsync.common.entity.BaseEntity;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.entity.TripMemberRole;
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
@Table(name = "invitations")
public class TripInvitation extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="trip_id",nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="invited_by_id",nullable = false)
    private User invitedBy;

    @Column(name = "invitee_email",nullable = false,length = 150)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private TripMemberRole proposedRole = TripMemberRole.VIEWER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private InvitationStatus status  = InvitationStatus.PENDING;

    @Column(name="expires_at",nullable = false)
    private LocalDateTime expiresAt;

    @Column(name="token_hash",unique = true,length = 64)
    private String tokenHash;

}
