package com.krs.community.model

data class RegisterModel(
    val userId: String,
    val expiresAt: String,
    val mobile: String,
    val role: String,
    val refreshToken: String?,
    val message: String
)
