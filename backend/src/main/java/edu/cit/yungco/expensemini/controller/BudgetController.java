package edu.cit.yungco.expensemini.controller;

import edu.cit.yungco.expensemini.dto.ApiResponse;
import edu.cit.yungco.expensemini.dto.BudgetRequest;
import edu.cit.yungco.expensemini.model.Budget;
import edu.cit.yungco.expensemini.service.BudgetService;
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
