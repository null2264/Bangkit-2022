package io.github.null2264.githubuser.lib

import com.google.gson.annotations.SerializedName

data class Token(
    @field:SerializedName("token_type")
    val type: String,
    @field:SerializedName("access_token")
    val key: String,
)