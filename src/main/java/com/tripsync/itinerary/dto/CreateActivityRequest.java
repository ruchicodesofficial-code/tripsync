package com.tripsync.itinerary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalTime;

public record CreateActivityRequest(

        @NotBlank(message = "Activity title is required")
        @Size(max=150)
        String title,

        @Size(max = 1000)
        String description,

        @Size(max = 200)
        String location,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @PositiveOrZero(message = "Estimated cost cannot be negative")
        BigDecimal estimatedCost,

        @PositiveOrZero(message = "Display order cannot be negative")
        Integer displayOrder
) {
}
