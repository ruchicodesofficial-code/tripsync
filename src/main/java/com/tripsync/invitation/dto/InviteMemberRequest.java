package com.tripsync.invitation.dto;

import com.tripsync.trip.entity.TripMemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InviteMemberRequest(

        @NotBlank(message = "Invitee email is required")
        @Email(message = "enter a valid email address")
        @Size(max = 150)
        String email,

        @NotNull(message = "Member role is required")
        TripMemberRole role
) {
}
