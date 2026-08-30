package com.krs.community.model

data class LoginModel(
    val authToken: String,
    val refreshToken: String?,
    val message: String,
    val userId: String? = null,
    val mobile: String? = null,
    val role: String? = null
)
