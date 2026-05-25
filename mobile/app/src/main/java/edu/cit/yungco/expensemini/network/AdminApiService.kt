package edu.cit.yungco.expensemini.network

import edu.cit.yungco.expensemini.network.models.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body

data class AdminUser(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val role: String
)

data class AdminStats(
    val total_users: Long,
    val total_expenses_count: Long,
    val total_system_money: Double
)

data class AdminCategory(
    val id: Long,
    val name: String,
    val description: String?
)

data class CategoryRequest(
    val name: String,
    val description: String?
)

interface AdminApiService {
    @GET("api/admin/users")
    suspend fun getAllUsers(): Response<ApiResponse<List<AdminUser>>>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<String>>

    @GET("api/admin/stats")
    suspend fun getStats(): Response<ApiResponse<AdminStats>>

    @GET("api/admin/categories")
    suspend fun getAllCategories(): Response<ApiResponse<List<AdminCategory>>>

    @POST("api/admin/categories")
    suspend fun createCategory(@Body request: CategoryRequest): Response<ApiResponse<AdminCategory>>

    @DELETE("api/admin/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): Response<ApiResponse<String>>
}
