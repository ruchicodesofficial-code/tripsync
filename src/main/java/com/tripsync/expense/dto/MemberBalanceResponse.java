package com.tripsync.expense.dto;

import java.math.BigDecimal;

public record MemberBalanceResponse(
        Long userId,
        String fullName,
        BigDecimal totalPaid,
        BigDecimal totalShare,
        BigDecimal settlementsSent,
        BigDecimal settlementsReceived,
        BigDecimal netBalance
) {
}
