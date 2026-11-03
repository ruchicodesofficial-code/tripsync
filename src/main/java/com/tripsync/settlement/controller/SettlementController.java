package com.tripsync.settlement.controller;

import com.tripsync.settlement.dto.CreateSettlementRequest;
import com.tripsync.settlement.repository.SettlementRepository;
import com.tripsync.settlement.service.SettlementService;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.tripsync.settlement.dto.CreateSettlementRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips/{tripId}/settlements")
public class SettlementController {
    private final UserRepository userRepository;
    private final SettlementService settlementService;
    @PostMapping("/settlements")
    public ResponseEntity<Void> createSettlement(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateSettlementRequest request,
            Authentication authentication
    ){
        User currentUser = userRepository.findByEmail(authentication.getName())
                        .orElseThrow(()->
                                new IllegalArgumentException("Authenticated user not found"));
        settlementService.createSettlement(tripId, currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
