package io.github.null2264.dicodingstories

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import io.github.null2264.dicodingstories.data.preference.PreferencesHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), DefaultLifecycleObserver {
    @Inject
    lateinit var prefs: PreferencesHelper

    override fun onCreate() {
        super<Application>.onCreate()

        // TODO - Language
        // prefs.getLang()

        prefs.getNightMode().onEach {
            AppCompatDelegate.setDefaultNightMode(
                when (it) {
                    true -> AppCompatDelegate.MODE_NIGHT_YES
                    false -> AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }.launchIn(ProcessLifecycleOwner.get().lifecycleScope)
    }
}