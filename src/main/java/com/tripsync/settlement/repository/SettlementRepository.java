package com.tripsync.settlement.repository;

import com.tripsync.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement,Long> {
    List<Settlement> findByTripId(Long tripId);
    List<Settlement> findByTripIdOrderBySettleAtDesc(Long tripId);
    List<Settlement> findByTripIdAndPayerId(Long tripId,Long payerId);
    List<Settlement> findByTripIdAndPayeeId(Long tripId,Long payeeId);
}
