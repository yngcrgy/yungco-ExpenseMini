package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
}
