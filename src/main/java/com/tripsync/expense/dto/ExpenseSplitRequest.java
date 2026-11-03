package com.tripsync.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ExpenseSplitRequest(
        @NotNull
        Long userId,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal shareAmount
) {

}
