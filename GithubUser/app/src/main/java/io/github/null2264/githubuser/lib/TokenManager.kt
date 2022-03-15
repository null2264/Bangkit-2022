package io.github.null2264.githubuser.lib

import android.content.SharedPreferences
import android.widget.EditText
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloHttpException
import io.github.null2264.githubuser.BasicQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun getToken(sharedPref: SharedPreferences): String? {
    return sharedPref.getString("token", null)
}

fun setToken(
    sharedPref: SharedPreferences,
    token: String,
): Boolean {
    sharedPref.edit().putString("token", token).apply()
    return true
}

suspend fun setToken(
    apolloClient: ApolloClient,
    sharedPref: SharedPreferences,
    editText: EditText,
): Boolean {
    var rt = false

    try {
        apolloClient.query(BasicQuery()).execute()

        sharedPref.edit().putString("token", editText.text.toString()).apply()
        rt = true
    } catch (_: ApolloHttpException) {
        withContext(Dispatchers.Main) {
            editText.error = "Invalid token!"
        }
    }

    return rt
}