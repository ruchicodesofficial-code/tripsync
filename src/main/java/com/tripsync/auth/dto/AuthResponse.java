package com.tripsync.auth.dto;

public record AuthResponse(

        String accessToken,
        String tokenType,
        Long expiresIn
) {
}
