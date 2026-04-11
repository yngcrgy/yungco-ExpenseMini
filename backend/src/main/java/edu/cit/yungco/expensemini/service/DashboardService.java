package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.dto.DashboardSummaryResponse;
import edu.cit.yungco.expensemini.model.Budget;
import edu.cit.yungco.expensemini.model.Expense;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.BudgetRepository;
import edu.cit.yungco.expensemini.repository.ExpenseRepository;
import edu.cit.yungco.expensemini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final ExpenseRepository expenseRepository;
        private final BudgetRepository budgetRepository;
        private final UserRepository userRepository;

        private User getAuthenticatedUser() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        }

        public DashboardSummaryResponse getSummary() {
                User user = getAuthenticatedUser();
                LocalDate now = LocalDate.now();
                LocalDate startOfMonth = now.withDayOfMonth(1);
                LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

                List<Expense> monthlyExpenses = expenseRepository.findByUserIdAndDateRange(user.getId(), startOfMonth,
                                endOfMonth);

                BigDecimal totalExpenses = monthlyExpenses.stream()
                                .map(Expense::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Budget budget = budgetRepository
                                .findByUserIdAndMonthAndYear(user.getId(), now.getMonthValue(), now.getYear())
                                .orElse(null);
                BigDecimal monthlyBudget = budget != null ? budget.getBudgetLimit() : BigDecimal.ZERO;
                BigDecimal remainingBudget = monthlyBudget.subtract(totalExpenses);

                String topCategory = "-";
                if (!monthlyExpenses.isEmpty()) {
                        topCategory = monthlyExpenses.stream()
                                        .collect(Collectors.groupingBy(Expense::getCategoryString,
                                                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount,
                                                                        BigDecimal::add)))
                                        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                                        .orElse("-");
                }

                BigDecimal avgDailySpending = BigDecimal.ZERO;
                int currentDay = now.getDayOfMonth();
                if (currentDay > 0 && totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                        avgDailySpending = totalExpenses.divide(new BigDecimal(currentDay), 2, RoundingMode.HALF_UP);
                }

                return DashboardSummaryResponse.builder()
                                .monthlyBudget(monthlyBudget)
                                .totalExpenses(totalExpenses)
                                .remainingBudget(remainingBudget)
                                .topCategory(topCategory)
                                .avgDailySpending(avgDailySpending)
                                .build();
        }
}
