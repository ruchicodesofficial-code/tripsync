package com.tripsync.trip.repository;

import com.tripsync.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripMemberRepository extends JpaRepository<TripMember,Long> {
    List<TripMember> findByTrip_Id(Long id);
    List<TripMember> findByUser_Id(Long userId);
    Optional<TripMember> findByTrip_IdAndUser_Id(Long tripId, Long userId);
    boolean existsByTrip_IdAndUser_Id(Long tripId, Long userId);
}
