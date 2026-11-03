package com.tripsync.user.dto;

import com.tripsync.user.entity.AccountStatus;
import com.tripsync.user.entity.Role;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        AccountStatus status
) {
}
