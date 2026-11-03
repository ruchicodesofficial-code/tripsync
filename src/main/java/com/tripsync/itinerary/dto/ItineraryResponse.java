package com.tripsync.itinerary.dto;

import java.time.LocalDate;
import java.util.List;

public record ItineraryResponse(
        Long id,
        Long tripId,
        LocalDate date,
        String notes,
        List<ActivityResponse> activities

) {
}
