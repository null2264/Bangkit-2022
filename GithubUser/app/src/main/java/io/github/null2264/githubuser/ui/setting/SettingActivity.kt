package io.github.null2264.githubuser.ui.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.preference.SettingPreferences
import io.github.null2264.githubuser.databinding.ActivitySettingBinding
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity(R.layout.activity_setting) {
    private val binding: ActivitySettingBinding by viewBinding(CreateMethod.INFLATE)

    @Inject
    lateinit var prefs: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.setting_fragment_container, PreferencesFragment())
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}