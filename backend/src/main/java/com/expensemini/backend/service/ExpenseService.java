package com.expensemini.backend.service;

import com.expensemini.backend.dto.ExpenseRequest;
import com.expensemini.backend.dto.ExpenseResponse;
import com.expensemini.backend.model.Category;
import com.expensemini.backend.model.Expense;
import com.expensemini.backend.model.Role;
import com.expensemini.backend.model.User;
import com.expensemini.backend.repository.CategoryRepository;
import com.expensemini.backend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseResponse createExpense(ExpenseRequest request, User user) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .category(category)
                .user(user)
                .build();

        expense = expenseRepository.save(expense);
        return mapToResponse(expense);
    }

    public List<ExpenseResponse> getAllExpenses(User user) {
        List<Expense> expenses;
        if (user.getRole() == Role.ADMIN) {
            expenses = expenseRepository.findAll();
        } else {
            expenses = expenseRepository.findByUserId(user.getId());
        }
        return expenses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (user.getRole() != Role.ADMIN && !expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to view this expense");
        }

        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (user.getRole() != Role.ADMIN && !expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this expense");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expense.setCategory(category);

        expense = expenseRepository.save(expense);
        return mapToResponse(expense);
    }

    public void deleteExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (user.getRole() != Role.ADMIN && !expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .date(expense.getDate())
                .receiptUrl(expense.getReceiptUrl())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .userId(expense.getUser().getId())
                .build();
    }
}
