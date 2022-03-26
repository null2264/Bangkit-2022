package io.github.null2264.githubuser.ui.main

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.updateMarginsRelative
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.common.GithubViewModelFactory
import io.github.null2264.githubuser.data.main.MainUsersViewModel
import io.github.null2264.githubuser.data.preference.SettingPreferences
import io.github.null2264.githubuser.databinding.ActivityMainBinding
import io.github.null2264.githubuser.lib.ActivityInjection
import io.github.null2264.githubuser.ui.auth.AuthActivity
import io.github.null2264.githubuser.ui.setting.SettingActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var viewModel: MainUsersViewModel
    private lateinit var navController: NavController
    private val binding: ActivityMainBinding by viewBinding(CreateMethod.INFLATE)
    private var initialized = false

    @Inject
    lateinit var prefs: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            if (prefs.getToken().first() == null && !initialized)
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
            else
                actuallyInit()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!initialized)
            actuallyInit()
    }

    override fun onStop() {
        super.onStop()
        if (initialized)
            binding.svMain.setQuery("", false)
    }

    private fun actuallyInit() {
        setSupportActionBar(binding.mainToolbar)

        lifecycleScope.launch {
            val factory = GithubViewModelFactory(ActivityInjection.provideRepository(applicationContext,
                prefs.getToken().first()!!))
            viewModel = ViewModelProvider(this@MainActivity, factory)[MainUsersViewModel::class.java]
        }

        // https://github.com/matthewzhang007/android-architecture-components/blob/3f9c56c/NavigationAdvancedSample/app/src/main/java/com/example/android/navigationadvancedsample/NavigationExtensions.kt#L205-L237
        // Workaround for FragmentContainerView not showing fragment properly
        var navHostFragment = supportFragmentManager.findFragmentByTag("homeFrag") as NavHostFragment?
        if (navHostFragment == null) {
            navHostFragment = NavHostFragment.create(R.navigation.main_navigation)
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_fragment_container, navHostFragment, "homeFrag")
                .commitNowAllowingStateLoss()
        }
        supportFragmentManager.beginTransaction()
            .attach(navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commit()
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomMainNav, navController)

        binding.apply {
            svMain.apply {
                // Stupid "bug" or behaviour where SearchView won't clear focus on launch with just clearFocus()
                isFocusedByDefault = false
                focusable = View.NOT_FOCUSABLE
                clearFocus()

                val searchEditFrame: LinearLayout = this.findViewById(R.id.search_edit_frame)
                (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0

                setOnQueryTextFocusChangeListener { _, hasFocus ->
                    val marginEnd = if (hasFocus) 18 else 10
                    val params = layoutParams as ViewGroup.MarginLayoutParams
                    params.updateMarginsRelative(end = marginEnd.px)
                    layoutParams = params
                    mainToolbar.menu.getItem(0).isVisible = !hasFocus
                }

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        when (navController.currentDestination?.id) {
                            R.id.nav_main_explore_fragment -> {
                                viewModel.setSearchQuery(query)
                            }
                            R.id.nav_main_favorites_fragment -> {
                                viewModel.setFavoriteQuery(query)
                            }
                            else -> return false
                        }
                        clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        return false
                    }
                })

                // Hacky solution for "onQueryTextCleared"
                findViewById<ImageView>(R.id.search_close_btn)?.setOnClickListener {
                    svMain.setQuery("", false)
                    when (navController.currentDestination?.id) {
                        R.id.nav_main_favorites_fragment -> {
                            viewModel.setFavoriteQuery(null)
                        }
                    }
                }
            }
        }

        initialized = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.main_settings -> {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            true
        }
        else -> false
    }

    private val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}