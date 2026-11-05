package com.tripsync.invitation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvitationEmailService {
    private final RestClient restClient;

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}")
    private String fromEmail;

    @Value("${app.invitation.base-url}")
    private String baseUrl;

    public void sendInvitation(String inviteeEmail,String tripTitle,String rawToken){
        String invitationUrl = baseUrl+"/invitations/accept?token="+rawToken;
        String emailBody ="You have been invited to join the trip: "
                +tripTitle +"\n\n"
                +"Open the following link to review and accept "
                +"the invitation:\n "
                +invitationUrl+"\n\n"+
                "This invitation expires after 48 hours\n\n"
                + "If you were not excepting this invitation, "
                + "you can ignore this email.";
        Map<String,Object> requestBody = Map.of(
                "from",fromEmail,
                "to", List.of(inviteeEmail),
                "subject","TripSync - Trip Invitation",
                "text",emailBody
        );
       restClient.post()
               .uri("https://api.resend.com/emails")
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization","Bearer "+resendApiKey)
               .body(requestBody)
               .retrieve()
               .toBodilessEntity();
    }
}
