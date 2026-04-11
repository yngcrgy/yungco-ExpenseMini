package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    List<Expense> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
