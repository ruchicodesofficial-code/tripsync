package com.tripsync.expense.repository;

import com.tripsync.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
List<Expense> findByTripIdOrderByExpenseDateDesc(Long tripId);
Optional<Expense> findByIdAndTripId(Long id, Long tripId);
List<Expense> findByPaidByIdAndTripId(Long userId, Long tripId);
}
