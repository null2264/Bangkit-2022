package io.github.null2264.dicodingstories.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import io.github.null2264.dicodingstories.lib.Common
import kotlinx.coroutines.flow.map
import io.github.null2264.dicodingstories.data.preference.PreferenceKeys as Keys

private val Context.dataStore by preferencesDataStore(name = "dicodingstories_data")

class PreferencesHelper(private val context: Context) {
    private val dataStore = context.dataStore

    fun getToken() = dataStore.data.map { prefs ->
        prefs[Keys.TOKEN] ?: ""
    }

    suspend fun setToken(newToken: String) = dataStore.edit { prefs ->
        prefs[Keys.TOKEN] = newToken
    }

    fun getNightMode() = dataStore.data.map { prefs ->
        prefs[Keys.NIGHT_MODE] ?: Common.isNightModeOn(context)
    }

    suspend fun setNightMode(newValue: Boolean) = dataStore.edit { prefs ->
        prefs[Keys.NIGHT_MODE] = newValue
    }

    fun getLang() = dataStore.data.map { prefs ->
        prefs[Keys.LANG] ?: ""
    }

    suspend fun setLang(newValue: String) = dataStore.edit { prefs ->
        prefs[Keys.LANG] = newValue
    }
}