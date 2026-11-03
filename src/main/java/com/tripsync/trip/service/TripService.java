package com.tripsync.trip.service;

import com.tripsync.common.exception.ResourceNotFoundException;
import com.tripsync.trip.dto.*;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.entity.TripMember;
import com.tripsync.trip.entity.TripMemberRole;
import com.tripsync.trip.repository.TripMemberRepository;
import com.tripsync.trip.repository.TripRepository;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripService {
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;

    public TripResponse createTrip(String authenticatedEmail, CreateTripRequest request){
        validateDates(request.startDate(),request.endDate());

        User owner = getUserByEmail(authenticatedEmail);
        Trip trip = new Trip();
        trip.setTitle(request.title().trim());
        trip.setDescription(request.description());
        trip.setDestination(request.destination());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        trip.setOwner(owner);
        Trip savedTrip = tripRepository.save(trip);

        TripMember ownerMembership = new TripMember();
        ownerMembership.setTrip(savedTrip);
        ownerMembership.setUser(owner);
        ownerMembership.setRole(TripMemberRole.OWNER);
        ownerMembership.setJoinedAt(LocalDateTime.now());
        tripMemberRepository.save(ownerMembership);
        return toTripResponse(savedTrip);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getMyTrips(String authenticatedEmail){
        User user = getUserByEmail(authenticatedEmail);
        List<TripMember> memberships = tripMemberRepository.findByUser_Id(user.getId());
        return memberships.stream()
                .map(TripMember::getTrip)
                .distinct()
                .map(this::toTripResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId, String authenticatedEmail){
        User user = getUserByEmail(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireMembership(trip.getId(),user.getId());
        return toTripResponse(trip);
    }

    public TripResponse updateTrip(Long tripId, String authenticatedEmail, UpdateTripRequest request){
        validateDates(request.startDate(),request.endDate());
        User user = getUserByEmail(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireOwnership(trip,user);
        trip.setTitle(request.title().trim());
        trip.setDescription(request.description());
        trip.setDestination(request.destination());
        trip.setStartDate(request.startDate());
        trip.setEndDate(request.endDate());
        return toTripResponse(tripRepository.save(trip));
    }

    public void deleteTrip(Long tripId,String authenticatedEmail){
        User user = getUserByEmail(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireOwnership(trip,user);
        tripMemberRepository.deleteAll(tripMemberRepository.findByTrip_Id(tripId));
        tripRepository.delete(trip);
    }

    @Transactional(readOnly = true)
    public List<TripMemberResponse> getMembers(Long tripId, String authenticatedEmail){
        User user = getUserByEmail(authenticatedEmail);
        getTrip(tripId);
        requireMembership(tripId,user.getId());
        return tripMemberRepository.findByTrip_Id(tripId)
                .stream()
                .map(member->new TripMemberResponse(
                        member.getUser().getId(),
                        member.getUser().getFullName(),
                        member.getUser().getEmail(),
                        member.getRole(),
                        member.getJoinedAt()
                )).toList();
    }

    public TripMemberResponse updateMemberRole(Long tripId, Long targetUserId, String authenticatedEmail, UpdateMemberRoleRequest request){
        User actor =getUserByEmail(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireOwnership(trip,actor);
        if (request.role()==TripMemberRole.OWNER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"OWNER role cannot be assigned through this endpoint");
        }
        TripMember target = tripMemberRepository.findByTrip_IdAndUser_Id(tripId,targetUserId)
                .orElseThrow(()-> new ResourceNotFoundException("Trip member not found"));
        if (target.getRole()==TripMemberRole.OWNER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The owner role cannot be changed here");
        }
        target.setRole(request.role());
        TripMember saved = tripMemberRepository.save(target);
        return  toMemberResponse(saved);
    }

    public void removeMember(Long tripId,Long targetUserId,String authenticatedEmail){
        User actor = getUserByEmail(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireOwnership(trip,actor);
        TripMember target = tripMemberRepository.findByTrip_IdAndUser_Id(tripId,targetUserId)
                .orElseThrow(()->new ResourceNotFoundException("Trip member not found"));
        if (target.getRole()==TripMemberRole.OWNER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Trip owner cannot be removed");
        }
        tripMemberRepository.delete(target);
    }

    private Trip getTrip(Long tripId){
        return tripRepository.findById(tripId)
                .orElseThrow(()->new ResourceNotFoundException("Trip not found with id: "+tripId));
    }
    private User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Authenticated user not found"));
    }
    private void requireMembership(Long tripId,Long userId){
        if (!tripMemberRepository.existsByTrip_IdAndUser_Id(tripId,userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not a member of this trip");
        }
    }

    private TripResponse toTripResponse(Trip trip){
        return new TripResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getDescription(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getOwner().getId(),
                trip.getOwner().getFullName()
        );

    }
    private void requireOwnership(Trip trip,User user){
        boolean isOwner = trip.getOwner().getId().equals(user.getId());
        if (!isOwner){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the trip owner can perform this action");
        }
    }

    private TripMemberResponse toMemberResponse(TripMember member){
    return new TripMemberResponse(
            member.getUser().getId(),
            member.getUser().getFullName(),
            member.getUser().getEmail(),
            member.getRole(),
            member.getJoinedAt()
    );
    }

    private void validateDates(LocalDate startDate, LocalDate endDate){
        if (endDate.isBefore(startDate)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"End date cannot be before start date");
        }
    }
}
