package com.tripsync.itinerary.controller;

import com.tripsync.itinerary.dto.*;
import com.tripsync.itinerary.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/itineraries")
public class ItineraryController {
    private final ItineraryService itineraryService;

    @PostMapping
    public ItineraryResponse createItinerary(@PathVariable Long tripId,
                                             @Valid @RequestBody CreateItineraryRequest request,
                                             Authentication authentication){
        return itineraryService.createItinerary(tripId,authentication.getName(),request);
    }

    @GetMapping
    public List<ItineraryResponse> getItineraries(@PathVariable Long tripId, Authentication authentication){
        return itineraryService.getItineraries(tripId,authentication.getName());
    }
    @GetMapping("/{itineraryId}")
    public ItineraryResponse getItinerary(@PathVariable Long tripId,
                                          @PathVariable Long itineraryId,
                                          Authentication authentication){
        return itineraryService.getItinerary(tripId,itineraryId,authentication.getName());
    }

    @PutMapping("/{itineraryId}")
    public ItineraryResponse updateItinerary(@PathVariable Long tripId,
                                             @PathVariable Long itineraryId,
                                             @Valid @RequestBody UpdateItineraryRequest request,
                                             Authentication authentication){
        return itineraryService.updateItinerary(tripId,itineraryId,authentication.getName(),request);
    }

    @DeleteMapping("/{itineraryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItinerary(@PathVariable Long tripId,
                                @PathVariable Long itineraryId,
                                Authentication authentication){
        itineraryService.deleteItinerary(tripId,itineraryId, authentication.getName());
    }

    @PostMapping("/{itineraryId}/activities")
    public ActivityResponse createActivity(@PathVariable Long tripId,
                                           @PathVariable Long itineraryId,
                                           @Valid @RequestBody CreateActivityRequest request,
                                           Authentication authentication){
        return itineraryService.createActivity(tripId,itineraryId, authentication.getName(),request);
    }

    @GetMapping("/{itineraryId}/activities")
    public List<ActivityResponse> getActivities(@PathVariable Long tripId,
                                                @PathVariable Long itineraryId,
                                                Authentication authentication){
        return itineraryService.getActivities(tripId,itineraryId,authentication.getName());
    }

    @PutMapping("/{itineraryId}/activities/{activityId}")
    public ActivityResponse updateActivity(@PathVariable Long tripId,
                                           @PathVariable Long itineraryId,
                                           @PathVariable Long activityId,
                                           @Valid @RequestBody UpdateActivityRequest request,
                                           Authentication authentication){
        return itineraryService.updateActivity(tripId,itineraryId,activityId,authentication.getName(),request);
    }

    @DeleteMapping("/{itineraryId}/activities/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable Long tripId,
                               @PathVariable Long itineraryId,
                               @PathVariable Long activityId,
                               Authentication authentication){
        itineraryService.deleteActivity(tripId,itineraryId,activityId,authentication.getName());
    }

}
