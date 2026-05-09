package edu.cit.yungco.expensemini.features.budget;

import edu.cit.yungco.expensemini.features.core.ApiResponse;
import edu.cit.yungco.expensemini.features.budget.BudgetRequest;
import edu.cit.yungco.expensemini.features.budget.Budget;
import edu.cit.yungco.expensemini.features.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<Budget>> setBudget(@RequestBody BudgetRequest request) {
        return ResponseEntity.ok(ApiResponse.success(budgetService.setBudget(request)));
    }
}
