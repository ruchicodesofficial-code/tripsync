package com.tripsync.trip.dto;

import com.tripsync.trip.entity.TripMemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(

        @NotNull(message = "Member role is required")
        TripMemberRole role
) {
}
