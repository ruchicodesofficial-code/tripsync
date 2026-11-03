package com.tripsync.document.dto;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        Long tripId,
        Long uploadedById,
        String originalFileName,
        String contentType,
        Long fileSize,
        LocalDateTime createdAt
) {
}
