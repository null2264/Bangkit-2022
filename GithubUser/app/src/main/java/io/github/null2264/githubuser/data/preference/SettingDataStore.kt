package io.github.null2264.githubuser.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingDataStore(private val dataStore: DataStore<Preferences>) : PreferenceDataStore() {
    override fun putBoolean(key: String, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(key)] = value
            }
        }
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return runBlocking {
            dataStore.data.map { prefs ->
                prefs[booleanPreferencesKey(key)] ?: defValue
            }.first()
        }
    }

    override fun putString(key: String, value: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(key)] = value!!
            }
        }
    }

    override fun getString(key: String, defValue: String?): String? {
        return runBlocking {
            dataStore.data.map { prefs ->
                prefs[stringPreferencesKey(key)] ?: defValue
            }.first()
        }
    }
}