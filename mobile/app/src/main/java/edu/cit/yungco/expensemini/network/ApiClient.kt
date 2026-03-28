package edu.cit.yungco.expensemini.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 points to localhost of the host machine running the emulator
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
