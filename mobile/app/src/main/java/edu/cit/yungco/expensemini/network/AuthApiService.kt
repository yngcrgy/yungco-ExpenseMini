package edu.cit.yungco.expensemini.network

import edu.cit.yungco.expensemini.network.models.AuthResponse
import edu.cit.yungco.expensemini.network.models.LoginRequest
import edu.cit.yungco.expensemini.network.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Any>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
