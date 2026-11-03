package com.tripsync.itinerary.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record ActivityResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal estimatedCost,
        Integer displayOrder
) {
}
