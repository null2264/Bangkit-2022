package io.github.null2264.dicodingstories.data.preference

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey("pref_token")
    val NIGHT_MODE = booleanPreferencesKey("pref_night_mode")
    val LANG = stringPreferencesKey("pref_language")
}