package com.tripsync.itinerary.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateItineraryRequest(

        @NotNull(message = "Itinerary date is required")
        LocalDate date,

        @Size(max=1000)
        String note
) {
}
