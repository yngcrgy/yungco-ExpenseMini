package edu.cit.yungco.expensemini.network

import edu.cit.yungco.expensemini.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ExpenseApiService {

    // Dashboard
    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(): Response<ApiResponse<DashboardSummary>>

    // Expenses
    @GET("api/expenses")
    suspend fun getExpenses(): Response<ApiResponse<List<Expense>>>

    @POST("api/expenses")
    suspend fun createExpense(@Body request: ExpenseRequest): Response<ApiResponse<Expense>>

    @PUT("api/expenses/{id}")
    suspend fun updateExpense(@Path("id") id: Long, @Body request: ExpenseRequest): Response<ApiResponse<Expense>>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<ApiResponse<Void>>

    // Budget
    @POST("api/budgets")
    suspend fun setBudget(@Body request: BudgetRequest): Response<ApiResponse<Any>>
}
