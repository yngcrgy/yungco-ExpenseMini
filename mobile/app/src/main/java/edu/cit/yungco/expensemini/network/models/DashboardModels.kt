package edu.cit.yungco.expensemini.network.models

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String?,
    val timestamp: String?
)

data class DashboardSummary(
    @SerializedName("monthly_budget") val monthlyBudget: Double,
    @SerializedName("total_expenses") val totalExpenses: Double,
    @SerializedName("remaining_budget") val remainingBudget: Double,
    @SerializedName("top_category") val topCategory: String?,
    @SerializedName("avg_daily_spending") val avgDailySpending: Double
)
