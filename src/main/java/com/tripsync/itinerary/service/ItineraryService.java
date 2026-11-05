package com.tripsync.itinerary.service;

import com.tripsync.itinerary.dto.*;
import com.tripsync.itinerary.entity.Activity;
import com.tripsync.itinerary.entity.Itinerary;
import com.tripsync.itinerary.repository.ActivityRepository;
import com.tripsync.itinerary.repository.ItineraryRepository;
import com.tripsync.trip.entity.Trip;
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
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class ItineraryService {
    private final ActivityRepository activityRepository;
    private final ItineraryRepository itineraryRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;

    public ItineraryResponse createItinerary(Long tripId, String authenticatedEmail,
                                             CreateItineraryRequest request){
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireCanEdit(trip,user);
        LocalDate date = request.date();
        if (itineraryRepository.existsByTrip_IdAndItineraryDate(tripId,date)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"An itinerary already exists for this date");
        }
        Itinerary  itinerary = new Itinerary();
        itinerary.setTrip(trip);
       itinerary.setItineraryDate(date);
       itinerary.setNotes(request.note());
       Itinerary saved = itineraryRepository.save(itinerary);
       return toItineraryResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ItineraryResponse> getItineraries(Long tripId,String authenticatedEmail){
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireTripAccess(trip,user);
        return itineraryRepository.findByTrip_IdOrderByItineraryDateAsc(tripId)
                .stream()
                .map(this::toItineraryResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public ItineraryResponse getItinerary(Long tripId,Long itineraryId,String authenticatedEmail){
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireCanEdit(trip,user);
        Itinerary itinerary = getItineraryForTrip(tripId,itineraryId);
        return toItineraryResponse(itinerary);
    }

    public ItineraryResponse updateItinerary(Long tripId, Long itineraryId, String authenticatedEmail, UpdateItineraryRequest request){
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireCanEdit(trip,user);
        Itinerary itinerary = getItineraryForTrip(tripId,itineraryId);
        if (itineraryRepository.existsByTrip_IdAndItineraryDateAndIdNot(tripId,request.date(),itineraryId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"An itinerary already exists for this date");
        }
        itinerary.setItineraryDate(request.date());
        itinerary.setNotes(request.note());
        return toItineraryResponse(itineraryRepository.save(itinerary));
    }
    public void deleteItinerary(Long tripId, Long itineraryId, String authenticatedEmail){
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireCanEdit(trip,user);
        Itinerary itinerary = getItineraryForTrip(tripId,itineraryId);
        itineraryRepository.delete(itinerary);
    }

    public ActivityResponse createActivity(Long tripId, Long itineraryId, String authenticatedEmail, CreateActivityRequest request){
       User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireCanEdit(trip,user);
        Itinerary itinerary = getItineraryForTrip(tripId,itineraryId);
        validateActivityTimes(request.startTime(),request.endTime());
        Activity activity = new Activity();
        activity.setItinerary(itinerary);
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setLocation(request.location());
        activity.setStartTime(request.startTime());
        activity.setEndTime(request.endTime());
        activity.setEstimatedCost(request.estimatedCost());
        activity.setDisplayOrder(request.displayOrder());
        return toActivityResponse(activityRepository.save(activity));
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> getActivities(Long tripId, Long itineraryId, String authenticatedEmail) {
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireTripAccess(trip,user);
        Itinerary itinerary = getItineraryForTrip(tripId,itineraryId);
        return activityRepository.findByItineraryIdOrderByDisplayOrderAsc(itinerary.getId())
                .stream()
                .map(this::toActivityResponse)
                .toList();

    }

    public ActivityResponse updateActivity(Long tripId,Long itineraryId,
                                           Long activityId,String authenticatedEmail,
                                           UpdateActivityRequest request){
        User user = getUser(authenticatedEmail);
        Trip trip = getTrip(tripId);
        requireCanEdit(trip,user);
        Itinerary itinerary = getItineraryForTrip(tripId,itineraryId);
        Activity activity = activityRepository.findByIdAndItineraryId(activityId,itineraryId)
                .orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Activity not found"));
        validateActivityTimes(request.startTime(),request.endTime());
        activity.setTitle(request.title());
        activity.setDescription(request.description());
        activity.setLocation(request.location());
        activity.setStartTime(request.startTime());
        activity.setEndTime(request.endTime());
        activity.setEstimatedCost(request.estimatedCost());
        activity.setDisplayOrder(request.displayOrder());
        return toActivityResponse(activityRepository.save(activity));
    }

    public void deleteActivity(Long tripId,Long itineraryId,
                               Long activityId,String authenticatedEmail){
            User user = getUser(authenticatedEmail);
            Trip trip = getTrip(tripId);
            requireCanEdit(trip,user);
          getItineraryForTrip(tripId,itineraryId);
          Activity activity = activityRepository.findByIdAndItineraryId(activityId,itineraryId)
                  .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Activity not found"));
          activityRepository.delete(activity);
    }
    private User getUser(String email){
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return userRepository.findByEmail(normalizedEmail).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED
        ,"Authenticated user not found"));
    }

    private Trip getTrip(Long tripId){
        return tripRepository.findById(tripId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Trip not found"));
    }

    private Itinerary getItineraryForTrip(Long tripId,Long itineraryId){
        Itinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"Itinerary not found"));
        if (!itinerary.getTrip().getId().equals(tripId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Itinerary not found for this trip");
        }
        return itinerary;
    }

    private void validateActivityTimes(java.time.LocalTime statTime, java.time.LocalTime endTime){
        if (!endTime.isAfter(statTime)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Activity end time must be after start time");
        }
    }
    private ItineraryResponse toItineraryResponse(Itinerary itinerary){
        List<ActivityResponse> activities = activityRepository.findByItineraryIdOrderByDisplayOrderAsc(itinerary.getId())
                .stream()
                .map(this::toActivityResponse)
                .toList();
        return new ItineraryResponse(itinerary.getId(), itinerary.getTrip().getId(),itinerary.getItineraryDate(),itinerary.getNotes(),activities);
    }
    public ActivityResponse toActivityResponse(Activity activity){
        return new ActivityResponse(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getLocation(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getEstimatedCost(),
                activity.getDisplayOrder()
                );
    }
    private void requireTripAccess(Trip trip,User user){
        if (!canAccessTrip(trip,user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not have access to this trip");
        }
    }
    private void requireCanEdit(Trip trip,User user){
        if (!canEditTrip(trip,user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"you do not have permission to edit this trip.");
        }
    }
    private boolean canAccessTrip(Trip trip,User user){
        if (trip.getOwner().getId().equals(user.getId())){
            return true;
        }
        return tripMemberRepository.existsByTrip_IdAndUser_Id(trip.getId(),user.getId());
    }

    private boolean canEditTrip(Trip trip,User user){
        if (trip.getOwner()!=null&& trip.getOwner().getId().equals(user.getId())){
            return true;
        }
        return tripMemberRepository.existsByTrip_IdAndUser_Id(trip.getId(),user.getId());
    }


}