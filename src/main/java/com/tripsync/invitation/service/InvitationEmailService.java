package com.tripsync.invitation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationEmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendInvitation(String inviteeEmail,String tripTitle,String rawToken){
        String invitationUrl = baseUrl+"/invitations/accept?token="+rawToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(inviteeEmail);
        message.setSubject("TripSync - Trip Invitation");
        message.setText("You have been invited to join the trip: "+tripTitle+"\n\n"
        +"Open the following link to review and accept "+"the invitation:\n "+invitationUrl+"\n\n"+
                "This invitation expires after 48 hours"+"\n\n"+
                "If you were not excepting this invitation, "+"you can ignore this email.");
        mailSender.send(message);
    }
}
