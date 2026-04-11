package edu.cit.yungco.expensemini.controller;

import edu.cit.yungco.expensemini.dto.ApiResponse;
import edu.cit.yungco.expensemini.dto.ExpenseRequest;
import edu.cit.yungco.expensemini.dto.ExpenseResponse;
import edu.cit.yungco.expensemini.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.createExpense(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses() {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getAllExpenses()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@PathVariable Long id,
            @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.updateExpense(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
