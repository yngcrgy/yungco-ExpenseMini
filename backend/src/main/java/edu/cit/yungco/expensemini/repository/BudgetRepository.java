package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Budget b WHERE b.user.id = :userId")
    void deleteAllByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
