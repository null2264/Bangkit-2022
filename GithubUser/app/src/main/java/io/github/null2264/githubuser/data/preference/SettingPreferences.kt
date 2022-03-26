package io.github.null2264.githubuser.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.null2264.githubuser.lib.Common
import io.github.null2264.githubuser.lib.Token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingPreferences @Inject constructor(@ApplicationContext private val ctx: Context) {
    val dataStore: DataStore<Preferences> = ctx.dataStore

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[Keys.THEME_KEY] ?: Common.isNightModeOn(ctx)
        }
    }

    suspend fun saveThemeSetting(isNightModeOn: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_KEY] = isNightModeOn
        }
    }

    fun getToken(): Flow<Token?> {
        return dataStore.data.map { prefs ->
            val fromPref = prefs[Keys.TOKEN_KEY]
            if (fromPref != null) Token("bearer", fromPref) else null
        }
    }

    suspend fun setToken(token: Token) {
        dataStore.edit { prefs ->
            prefs[Keys.TOKEN_KEY] = token.token
        }
    }

    private object Keys {
        val THEME_KEY = booleanPreferencesKey("night_theme")
        val TOKEN_KEY = stringPreferencesKey("token")
    }
}