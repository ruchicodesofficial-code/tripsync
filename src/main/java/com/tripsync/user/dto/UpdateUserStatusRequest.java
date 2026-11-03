package com.tripsync.user.dto;

import com.tripsync.user.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "Account status is required")
        AccountStatus status
) {
}
