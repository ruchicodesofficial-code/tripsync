package com.tripsync.invitation.dto;

import com.tripsync.invitation.entity.InvitationStatus;
import com.tripsync.trip.entity.TripMemberRole;

import java.time.LocalDateTime;

public record InvitationResponse(
        Long invitationId,
        Long tripId,
        String inviteeEmail,
        TripMemberRole proposedRole,
        InvitationStatus status,
        LocalDateTime expiresAt

) {
}
