package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.dto.ExpenseRequest;
import edu.cit.yungco.expensemini.dto.ExpenseResponse;
import edu.cit.yungco.expensemini.model.Expense;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.ExpenseRepository;
import edu.cit.yungco.expensemini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Long getCategoryIdValue(Integer reqCatId, String reqCat) {
        if (reqCatId != null && reqCatId != 0)
            return reqCatId.longValue();
        if ("Food".equalsIgnoreCase(reqCat))
            return 1L;
        if ("Transport".equalsIgnoreCase(reqCat))
            return 2L;
        if ("School".equalsIgnoreCase(reqCat))
            return 3L;
        if ("Personal".equalsIgnoreCase(reqCat))
            return 4L;
        return 5L; // Other
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {
        User user = getAuthenticatedUser();
        Expense expense = Expense.builder()
                .user(user)
                .title(request.getTitle())
                .amount(request.getAmount())
                .dbCategoryId(getCategoryIdValue(request.getCategoryId(), request.getCategory()))
                .expenseDate(request.getExpenseDate())
                .notes(request.getNotes())
                .build();

        expense = expenseRepository.save(expense);
        return mapToResponse(expense);
    }

    public List<ExpenseResponse> getAllExpenses() {
        User user = getAuthenticatedUser();
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDbCategoryId(getCategoryIdValue(request.getCategoryId(), request.getCategory()));
        if (request.getExpenseDate() != null)
            expense.setExpenseDate(request.getExpenseDate());
        if (request.getNotes() != null)
            expense.setNotes(request.getNotes());

        return mapToResponse(expenseRepository.save(expense));
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    private ExpenseResponse mapToResponse(Expense e) {
        return ExpenseResponse.builder()
                .expenseId(e.getId())
                .title(e.getTitle())
                .amount(e.getAmount())
                .category(e.getCategoryString())
                .expenseDate(e.getExpenseDate())
                .build();
    }
}
