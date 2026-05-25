package edu.cit.yungco.expensemini.service;

import edu.cit.yungco.expensemini.dto.AdminExpenseResponse;
import edu.cit.yungco.expensemini.model.Expense;
import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.repository.ExpenseRepository;
import edu.cit.yungco.expensemini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import edu.cit.yungco.expensemini.dto.AdminStatsResponse;
import edu.cit.yungco.expensemini.dto.CategoryRequest;
import edu.cit.yungco.expensemini.model.Category;
import edu.cit.yungco.expensemini.repository.CategoryRepository;
import edu.cit.yungco.expensemini.repository.BudgetRepository;
import edu.cit.yungco.expensemini.repository.NotificationRepository;
import edu.cit.yungco.expensemini.repository.SubscriptionRepository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<AdminExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream().map(expense -> AdminExpenseResponse.builder()
                .expenseId(expense.getId())
                .userEmail(expense.getUser().getEmail())
                .userName(expense.getUser().getFirstName() + " " + expense.getUser().getLastName())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .category(expense.getCategoryString())
                .expenseDate(expense.getExpenseDate() != null ? expense.getExpenseDate() : expense.getLegacyDate())
                .notes(expense.getNotes())
                .build()
        ).collect(Collectors.toList());
    }

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalExpensesCount = expenseRepository.count();
        List<Expense> allExpenses = expenseRepository.findAll();
        BigDecimal totalMoney = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalExpensesCount(totalExpensesCount)
                .totalSystemMoney(totalMoney)
                .build();
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        expenseRepository.deleteAllByUserId(userId);
        budgetRepository.deleteAllByUserId(userId);
        notificationRepository.deleteAllByUserId(userId);
        subscriptionRepository.deleteAllByUserId(userId);
        userRepository.delete(user);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
