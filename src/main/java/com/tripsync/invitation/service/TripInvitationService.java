package com.tripsync.invitation.service;

import com.tripsync.invitation.dto.InvitationResponse;
import com.tripsync.invitation.dto.InviteMemberRequest;
import com.tripsync.invitation.entity.InvitationStatus;
import com.tripsync.invitation.entity.TripInvitation;
import com.tripsync.invitation.repository.TripInvitationRepository;
import com.tripsync.notification.entity.NotificationType;
import com.tripsync.notification.service.TripNotificationService;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.entity.TripMember;
import com.tripsync.trip.entity.TripMemberRole;
import com.tripsync.trip.repository.TripMemberRepository;
import com.tripsync.trip.repository.TripRepository;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TripInvitationService {
    private final TripInvitationRepository tripInvitationRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;
    private final InvitationEmailService emailService;
    private final TripNotificationService notificationService;

    @Value("${app.invitation.expiration-hours:48}")
    private int expirationHours;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public InvitationResponse inviteMember(Long tripId, InviteMemberRequest request, String authenticatedEmail){
        User inviter = getUser(authenticatedEmail);
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Trip not found."));

        if (trip.getOwner()==null||!trip.getOwner().getId().equals(inviter.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the trip owner can invite members");
        }
        String inviteeEmail = request.email().trim();
        if (inviteeEmail.equalsIgnoreCase(inviter.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot invite yourself");
        }
        if (request.role()== TripMemberRole.OWNER){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"OWNER role cannot be assigned through an invitation");
        }
        User exstingUser = userRepository.findByEmail(inviteeEmail)
                .orElse(null);
        if (exstingUser!=null&&tripMemberRepository.existsByTrip_IdAndUser_Id(tripId,exstingUser.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User is already a member of this trip");
        }
        boolean pendingInvitation = tripInvitationRepository.existsByTrip_IdAndInviteeEmailIgnoreCaseAndStatus(tripId,inviteeEmail, InvitationStatus.PENDING);
        if (pendingInvitation){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"A pending invitation already exists for this email");
        }
        String rawToken = generateToken();
        TripInvitation invitation = new TripInvitation();
        invitation.setTrip(trip);
        invitation.setInvitedBy(inviter);
        invitation.setInviteeEmail(inviteeEmail);
        invitation.setProposedRole(request.role());
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));
        invitation.setTokenHash(hashToken(rawToken));
        TripInvitation savedInvitation = tripInvitationRepository.save(invitation);
        emailService.sendInvitation(inviteeEmail,trip.getTitle(),rawToken);
        if (exstingUser!=null){
            notificationService.createNotification(exstingUser,
                    trip,
                    NotificationType.INVITATION_RECEIVED,
                    "New trip invitation",
                    "You have been invited to join the trip: "+trip.getTitle());
        }
        return toResponse(savedInvitation);
    }

    @Transactional
    public InvitationResponse acceptInvitation(String  token, String authenticatedEmail){
        User user = getUser(authenticatedEmail);
        TripInvitation invitation = getInvitationByToken(token);
        validateInvitation(invitation,user);
        Trip trip = invitation.getTrip();
        if (tripMemberRepository.existsByTrip_IdAndUser_Id(trip.getId(),user.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"You are already a member of this trip");
        }
        TripMember member = new TripMember();
        member.setTrip(trip);
        member.setUser(user);
        member.setRole(invitation.getProposedRole());
        member.setJoinedAt(LocalDateTime.now());
        tripMemberRepository.save(member);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        TripInvitation savedInvitation= tripInvitationRepository.save(invitation);
        notificationService.createNotification(
                trip.getOwner(),
                trip,
                NotificationType.INVITATION_ACCEPTED,
                "Invitation accepted",
                user.getFullName() + " accepted the invitation for trip: "+trip.getTitle()
        );
        return toResponse(savedInvitation);
    }

    @Transactional
    public InvitationResponse rejectInvitation(String  token, String authenticatedEmail){
        User user = getUser(authenticatedEmail);
        TripInvitation invitation = getInvitationByToken(token);
        validateInvitation(invitation,user);
        invitation.setStatus(InvitationStatus.REJECTED);
        Trip trip = invitation.getTrip();
        TripInvitation savedInvitation= tripInvitationRepository.save(invitation);
        notificationService.createNotification(
                trip.getOwner(),
                trip,
                NotificationType.INVITATION_REJECTED,
                "Invitation declined",
                user.getFullName() + " declined the invitation for trip: "+trip.getTitle()
        );
        return toResponse(savedInvitation);
    }

    private User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
    }
private TripInvitation getInvitationByToken(String token){
        if (token==null||token.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invitation token is required"
            );
        }
        String tokenHash = hashToken(token);
        return tripInvitationRepository.findByTokenHash(tokenHash).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid invitation token"));
}
private void validateInvitation(TripInvitation invitation,User user){
        if (!invitation.getInviteeEmail().equalsIgnoreCase(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This invitation belongs to another email address");
        }
       if (invitation.getStatus()!=InvitationStatus.PENDING){
           throw new ResponseStatusException(HttpStatus.CONFLICT,"Invitation has already been processed");
       }
       if (!invitation.getExpiresAt().isAfter(LocalDateTime.now())){
           throw new ResponseStatusException(HttpStatus.GONE,"Invitation has expired");
       }
}

private String generateToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
}

private String hashToken(String token){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash= digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        }catch (NoSuchAlgorithmException exception){
            throw new IllegalStateException("SHA-256 algorithm is unavailable",exception);
        }
}
private InvitationResponse toResponse(TripInvitation invitation){
        return new InvitationResponse(
                invitation.getId(),
                invitation.getTrip().getId(),
                invitation.getInviteeEmail(),
                invitation.getProposedRole(),
                invitation.getStatus(),
                invitation.getExpiresAt()
        );
}
}

