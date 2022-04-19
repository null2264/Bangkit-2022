package io.github.null2264.dicodingstories.data.preference

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.preference.Preference
import io.github.null2264.dicodingstories.data.preference.PreferenceKeys as Keys

class PreferencesHelper(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getToken() = prefs.getString(Keys.TOKEN, null)
    fun setToken(newToken: String?) = prefs.edit().putString(Keys.TOKEN, newToken).commit()
}