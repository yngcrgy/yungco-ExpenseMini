package edu.cit.yungco.expensemini.network.models

data class AuthResponse(
    val token: String,
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)
