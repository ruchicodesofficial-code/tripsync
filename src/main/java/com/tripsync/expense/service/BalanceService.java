package com.tripsync.expense.service;

import com.tripsync.expense.dto.MemberBalanceResponse;
import com.tripsync.expense.entity.Expense;
import com.tripsync.expense.entity.ExpenseSplit;
import com.tripsync.settlement.entity.Settlement;
import com.tripsync.expense.repository.ExpenseRepository;
import com.tripsync.expense.repository.ExpenseSplitRepository;
import com.tripsync.settlement.repository.SettlementRepository;
import com.tripsync.trip.entity.TripMember;
import com.tripsync.trip.repository.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final TripMemberRepository tripMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final SettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    public List<MemberBalanceResponse> getBalances(Long tripId){
        List<TripMember> members = tripMemberRepository.findByTrip_Id(tripId);
        List<Expense> expenses = expenseRepository.findByTripIdOrderByExpenseDateDesc(tripId);
        List<Settlement> settlements = settlementRepository.findByTripIdOrderBySettleAtDesc(tripId);
        List<MemberBalanceResponse> results = new ArrayList<>();
        for (TripMember member:members){
            Long userId = member.getUser().getId();
            BigDecimal totalPaid = expenses.stream()
                    .filter(e->e.getPaidBy().getId().equals(userId))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
            List<ExpenseSplit> splits = splitRepository.findByExpenseTripIdAndUserId(tripId,userId);

            BigDecimal totalShare = splits.stream()
                    .map(ExpenseSplit::getShareAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);

            BigDecimal settlementsSent = settlements.stream()
                    .filter(s-> s.getPayer().getId().equals(userId))
                    .map(Settlement::getAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);

            BigDecimal settlementReceived = settlements.stream()
                    .filter(s-> s.getPayee().getId().equals(userId))
                    .map(Settlement::getAmount)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
            //Net Balance = Paid Amount - Share Amount + Settlements Sent - Settlements Received
            BigDecimal netBalance = totalPaid
                    .subtract(totalShare)
                    .add(settlementsSent)
                    .subtract(settlementReceived);
            results.add(new MemberBalanceResponse(
                    userId,
                    member.getUser().getFullName(),
                    totalPaid,
                    totalShare,
                    settlementsSent,
                    settlementReceived,
                    netBalance
            ));
        }
        return results;
    }

}
