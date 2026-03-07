package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    List<Expense> findByCategoryId(Long categoryId);
}
