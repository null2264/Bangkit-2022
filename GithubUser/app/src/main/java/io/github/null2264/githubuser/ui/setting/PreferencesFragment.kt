package io.github.null2264.githubuser.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.preference.SettingDataStore
import io.github.null2264.githubuser.lib.Common

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Probably unsafe, but this is the only workaround I found that allows me to use DataStore in PreferenceScreen
        val prefs = (activity as SettingActivity).prefs
        preferenceManager.preferenceDataStore = SettingDataStore(prefs.dataStore)

        val ctx = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(ctx)

        val displayCat = PreferenceCategory(ctx)
        displayCat.apply {
            title = getString(R.string.display_category)
        }
        val nightModeSwitch = SwitchPreferenceCompat(ctx)
        nightModeSwitch.apply {
            key = "night_theme"
            title = getString(R.string.settings_night_mode)
            setDefaultValue(Common.isNightModeOn(ctx))
        }
        screen.addPreference(displayCat)
        displayCat.addPreference(nightModeSwitch)

        preferenceScreen = screen
    }
}