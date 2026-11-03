package com.tripsync.notification.controller;

import com.tripsync.notification.dto.NotificationResponse;
import com.tripsync.notification.service.TripNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/notifications")
public class TripNotificationController {
    private final TripNotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getMyNotifications(Authentication authentication){
        return notificationService.getMyNotifications(getEmail(authentication));
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(@PathVariable Long notificationId,Authentication authentication){
        return notificationService.markAsRead(notificationId,getEmail(authentication));
    }

    private String  getEmail(Authentication authentication){
        if (authentication==null||!authentication.isAuthenticated()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication is required");
        }
        return authentication.getName();
    }
}
