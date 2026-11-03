package com.tripsync.trip.dto;

import java.time.LocalDate;

public record TripResponse(

        Long id,
        String title,
        String description,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        Long ownerId,
        String ownerName
) {
}
