package com.tripsync.invitation.controller;

import com.tripsync.invitation.dto.InvitationResponse;
import com.tripsync.invitation.dto.InviteMemberRequest;
import com.tripsync.invitation.service.TripInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/invitations")
public class TripInvitationController {
    private final TripInvitationService tripInvitationService;

    //create invitation and send email
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponse inviteMember(@PathVariable Long tripId,
                                           @Valid @RequestBody InviteMemberRequest request, Authentication authentication){
        return tripInvitationService.inviteMember(tripId,request,getAuthenticationEmail(authentication));

    }

    //Accept invitation
    @PostMapping("/accept")
    public InvitationResponse acceptInvitation(@PathVariable Long tripId, @RequestParam String token,Authentication authentication){
        InvitationResponse response = tripInvitationService.acceptInvitation(token,getAuthenticationEmail(authentication));
        if (!response.tripId().equals(tripId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invitation does not belongs to this trip");
        }
        return response;
    }

    //reject invitation
    @PostMapping("/reject")
    public InvitationResponse rejectInvitation(@PathVariable Long tripId,@RequestParam String token,Authentication authentication){
        InvitationResponse response = tripInvitationService.rejectInvitation(token,getAuthenticationEmail(authentication));
        if (!response.tripId().equals(tripId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invitation does not belongs to this trip");
        }
        return response;
    }

    private String getAuthenticationEmail(Authentication authentication){
        if (authentication==null||!authentication.isAuthenticated()){
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication is required");
        }
        return authentication.getName();
    }

}
