package com.tripsync.user.controller;

import com.tripsync.user.dto.ChangePasswordRequest;
import com.tripsync.user.dto.RegisterRequest;
import com.tripsync.user.dto.UpdateUserStatusRequest;
import com.tripsync.user.dto.UserResponse;
import com.tripsync.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChangePasswordRequest request){
        userService.changePassword(
                userDetails.getUsername(),request
        );
        return ResponseEntity.noContent().build();
    }

}
