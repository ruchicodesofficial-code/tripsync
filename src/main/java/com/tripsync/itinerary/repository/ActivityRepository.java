package com.tripsync.itinerary.repository;

import com.tripsync.itinerary.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,Long> {

    List<Activity> findByItineraryIdOrderByDisplayOrderAsc(Long itineraryId);
    List<Activity> findByItineraryTripOrderByDisplayOrderAsc(Long tripId);
    Optional<Activity> findByIdAndItineraryId(Long id, Long itineraryId);
}
