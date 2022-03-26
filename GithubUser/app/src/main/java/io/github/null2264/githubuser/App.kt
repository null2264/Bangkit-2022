package io.github.null2264.githubuser

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import io.github.null2264.githubuser.data.preference.SettingPreferences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), DefaultLifecycleObserver {
    @Inject
    lateinit var prefs: SettingPreferences

    override fun onCreate() {
        super<Application>.onCreate()

        prefs.getThemeSetting().onEach {
            AppCompatDelegate.setDefaultNightMode(
                when (it) {
                    true -> AppCompatDelegate.MODE_NIGHT_YES
                    false -> AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }.launchIn(ProcessLifecycleOwner.get().lifecycleScope)
    }

}