package com.tripsync.trip.controller;

import com.tripsync.trip.dto.*;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request,
                                                   Authentication authentication){
        TripResponse response = tripService.createTrip(authentication.getName(),request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getMyTrips(Authentication authentication){
        return ResponseEntity.ok(tripService.getMyTrips(authentication.getName()));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long tripId, Authentication authentication){
        return ResponseEntity.ok(tripService.getTripById(tripId,authentication.getName()));
    }

    @PatchMapping("/{tripId}")
    public ResponseEntity<TripResponse> updateTrip(@PathVariable Long tripId,
        @Valid @RequestBody UpdateTripRequest request,Authentication authentication){
        return ResponseEntity.ok(tripService.updateTrip(tripId,authentication.getName(),request));

    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId,Authentication authentication){
        tripService.deleteTrip(tripId,authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tripId}/members")
    public ResponseEntity<List<TripMemberResponse>> getMembers(@PathVariable Long tripId, Authentication authentication){
        return ResponseEntity.ok(tripService.getMembers(tripId, authentication.getName()));
    }

    @PatchMapping("/{tripId}/members/{userId}/role")
    public ResponseEntity<TripMemberResponse> updateMemberRole(@PathVariable Long tripId,@PathVariable Long userId,
                                                               @Valid @RequestBody UpdateMemberRoleRequest request,Authentication authentication){
        return ResponseEntity.ok(tripService.updateMemberRole(tripId,userId,authentication.getName(),request));
    }

    @DeleteMapping("/{tripId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long tripId,@PathVariable Long userId,Authentication authentication){
        tripService.removeMember(tripId,userId, authentication.getName());

   return ResponseEntity.noContent().build();
    }
}
