package com.tripsync.settlement.service;

import com.tripsync.settlement.dto.CreateSettlementRequest;
import com.tripsync.settlement.entity.Settlement;
import com.tripsync.settlement.repository.SettlementRepository;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.repository.TripMemberRepository;
import com.tripsync.trip.repository.TripRepository;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public Settlement createSettlement(
            Long tripId,
            Long payerId,
            CreateSettlementRequest request
    ){
        if (payerId.equals(request.payeeId())){
            throw new IllegalArgumentException("Payer and payee cannot be same user");
        }
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(()->
                        new IllegalArgumentException("Trip not found"));

        if (!tripMemberRepository.existsByTrip_IdAndUser_Id(tripId,payerId)){
            throw new IllegalArgumentException("Payer is not a member of this trip");
        }
        if (!tripMemberRepository.existsByTrip_IdAndUser_Id(tripId,request.payeeId())){
            throw new IllegalArgumentException("Payee is not a member of this trip");
        }
        User payer = userRepository.findById(payerId)
                .orElseThrow(()->
                        new IllegalArgumentException("Payer not found"));
        User payee = userRepository.findById(request.payeeId())
                .orElseThrow(()->
                        new IllegalArgumentException("Payee not found"));

        Settlement settlement = new Settlement();
        settlement.setTrip(trip);
        settlement.setPayer(payer);
        settlement.setPayee(payee);
        settlement.setAmount(request.amount());
        settlement.setNote(request.note());
        settlement.setSettleAt(LocalDateTime.now());
        return settlementRepository.save(settlement);
    }

}
