package com.tripsync.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTripRequest(
        @NotBlank(message = "Trip title is required")
        @Size(max=150)
        String title,

        @Size(max = 1000)
        String description,

        @NotBlank(message = "Destination is required")
        @Size(max=200)
        String destination,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate


) {
}
