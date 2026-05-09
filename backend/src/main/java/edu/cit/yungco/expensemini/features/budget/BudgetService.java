package edu.cit.yungco.expensemini.features.budget;

import edu.cit.yungco.expensemini.features.budget.BudgetRequest;
import edu.cit.yungco.expensemini.features.budget.Budget;
import edu.cit.yungco.expensemini.features.user.User;
import edu.cit.yungco.expensemini.features.budget.BudgetRepository;
import edu.cit.yungco.expensemini.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Budget setBudget(BudgetRequest request) {
        User user = getAuthenticatedUser();
        Optional<Budget> existing = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), request.getMonth(),
                request.getYear());

        if (existing.isPresent()) {
            Budget b = existing.get();
            b.setBudgetLimit(request.getBudgetLimit());
            return budgetRepository.save(b);
        } else {
            return budgetRepository.save(Budget.builder()
                    .user(user)
                    .month(request.getMonth())
                    .year(request.getYear())
                    .budgetLimit(request.getBudgetLimit())
                    .build());
        }
    }
}
