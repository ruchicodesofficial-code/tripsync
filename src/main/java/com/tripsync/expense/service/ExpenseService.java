package com.tripsync.expense.service;

import com.tripsync.expense.dto.CreateExpenseRequest;
import com.tripsync.expense.dto.ExpenseSplitRequest;
import com.tripsync.expense.entity.Expense;
import com.tripsync.expense.entity.ExpenseSplit;
import com.tripsync.expense.repository.ExpenseRepository;
import com.tripsync.expense.repository.ExpenseSplitRepository;
import com.tripsync.trip.entity.Trip;
import com.tripsync.trip.repository.TripMemberRepository;
import com.tripsync.trip.repository.TripRepository;
import com.tripsync.user.entity.User;
import com.tripsync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private  final UserRepository userRepository;

    @Transactional
    public Expense createExpense(Long tripId, Long paidByUserId, CreateExpenseRequest request){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(()-> new IllegalArgumentException("Trip not found"));
        if (!tripMemberRepository.existsByTrip_IdAndUser_Id(tripId,paidByUserId)){
            throw new IllegalArgumentException("Payment user is not a trip member");
        }
        BigDecimal splitTotal = BigDecimal.ZERO;
        Set<Long> userIds = new HashSet<>();
        for (ExpenseSplitRequest split: request.splits()){
            if (split.shareAmount().signum()<0){
                throw new IllegalArgumentException("Split amount cannot be negative");
            }
            if (!userIds.add(split.userId())){
                throw new IllegalArgumentException("Duplicate user in expense splits");
            }
            if (!tripMemberRepository.existsByTrip_IdAndUser_Id(tripId,split.userId())){
                throw new IllegalArgumentException("Split user is not a trip member");
            }
            splitTotal = splitTotal.add(split.shareAmount());
        }
        if (splitTotal.compareTo(request.amount())!=0){
            throw new IllegalArgumentException("Split total must equal expense amount");
        }
        User paidBy = userRepository.findById(paidByUserId)
                .orElseThrow(()->
                        new IllegalArgumentException("User not found"));

        Expense expense = new Expense();
        expense.setTrip(trip);
        expense.setPaidBy(paidBy);
        expense.setTitle(request.title());
        expense.setDescription(request.description());
        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        Expense savedExpense = expenseRepository.save(expense);
        for (ExpenseSplitRequest split: request.splits()){
            User splitUser = userRepository.findById(split.userId())
                    .orElseThrow(()-> new IllegalArgumentException("User not found"));
            ExpenseSplit expenseSplit = new ExpenseSplit();
            expenseSplit.setExpense(savedExpense);
            expenseSplit.setUser(splitUser);
            expenseSplit.setShareAmount(split.shareAmount());
            splitRepository.save(expenseSplit);
        }
        return savedExpense;
    }


}
