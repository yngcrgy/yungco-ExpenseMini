package edu.cit.yungco.expensemini.network.models

import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("expense_id") val expenseId: Long?,
    val id: Long?,
    val title: String,
    val amount: Double,
    val category: String?,
    @SerializedName("expense_date") val expenseDate: String?,
    val notes: String?
) {
    fun getDisplayId(): Long = expenseId ?: id ?: 0
}

data class ExpenseRequest(
    val title: String,
    val amount: Double,
    @SerializedName("category_id") val categoryId: Int?,
    val category: String?,
    val notes: String?,
    @SerializedName("expense_date") val expenseDate: String?
)

data class BudgetRequest(
    val month: Int,
    val year: Int,
    @SerializedName("budget_limit") val budgetLimit: Double
)
