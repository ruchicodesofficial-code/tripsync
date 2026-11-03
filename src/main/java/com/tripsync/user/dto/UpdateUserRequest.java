package com.tripsync.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Full name is required")
        @Size(max=100)
        String fullName,
        @NotBlank(message = "Invalid email format")
        @Email(message = "Invalid email format")
        @Size(max=150)
        String email
) {
}
