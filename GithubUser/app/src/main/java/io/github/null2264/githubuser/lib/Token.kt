package io.github.null2264.githubuser.lib

import android.content.SharedPreferences
import com.apollographql.apollo3.exception.ApolloHttpException
import com.google.gson.annotations.SerializedName
import io.github.null2264.githubuser.BasicQuery
import io.github.null2264.githubuser.lib.api.Apollo

data class Token(
    @field:SerializedName("token_type")
    val type: String,
    @field:SerializedName("access_token")
    var token: String,
) {
    override fun toString(): String = "$type $token"

    fun toSharedPreference(sharedPref: SharedPreferences): Boolean {
        sharedPref.edit().putString("token", token).apply()
        return true
    }

    suspend fun validateToken(): Boolean {
        return try {
            Apollo.getInstance(this).query(BasicQuery()).execute()
            true
        } catch (_: ApolloHttpException) {
            false
        }
    }

    companion object {
        @JvmStatic
        fun fromSharedPreference(sharedPref: SharedPreferences): Token? {
            val token = sharedPref.getString("token", null) ?: return null
            return Token("bearer", token)
        }
    }
}