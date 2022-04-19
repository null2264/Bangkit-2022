package io.github.null2264.dicodingstories.data.preference

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.preference.Preference
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import io.github.null2264.dicodingstories.data.preference.PreferenceKeys as Keys

@ExperimentalCoroutinesApi
class PreferencesHelper(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun getToken() = prefs.getString(Keys.TOKEN, null)
    fun setToken(newToken: String?) = prefs.edit().putString(Keys.TOKEN, newToken).commit()

    fun nightMode() = flowPrefs.getBoolean(Keys.NIGHT_MODE, false /* TODO - Get device's current theme */)
    fun lang() = flowPrefs.getString(Keys.LANG, "")
}