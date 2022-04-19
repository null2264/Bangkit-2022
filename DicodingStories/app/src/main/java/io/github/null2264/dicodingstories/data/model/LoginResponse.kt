package io.github.null2264.dicodingstories.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    @field:SerializedName("userId")
    val id: String,
    val name: String,
    val token: String
)