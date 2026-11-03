package com.tripsync.itinerary.repository;

import com.tripsync.itinerary.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItineraryRepository extends JpaRepository<Itinerary,Long> {

    List<Itinerary> findByTrip_IdOrderByItineraryDateAsc(Long tripId);
    Optional<Itinerary> findByTrip_IdAndItineraryDate(Long tripId, LocalDate itineraryDate);
    boolean existsByTrip_IdAndItineraryDate(Long tripId,LocalDate itineraryDate);
    boolean existsByTrip_IdAndItineraryDateAndIdNot(Long tripId,LocalDate itineraryDate, Long id);
}
