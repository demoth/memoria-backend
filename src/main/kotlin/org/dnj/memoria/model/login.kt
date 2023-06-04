package org.dnj.memoria.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class SignupRequest(
    val username: String,
    val password: String,
    val promo: String
)

data class LoginResponse(
    val username: String,
    val jwt: String,
    val userspaces: Collection<SpaceDto>
)