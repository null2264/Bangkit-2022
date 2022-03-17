package io.github.null2264.githubuser.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.updateMarginsRelative
import androidx.lifecycle.ViewModelProvider
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.TokenViewModelFactory
import io.github.null2264.githubuser.data.UsersRecyclerInterface
import io.github.null2264.githubuser.data.main.MainUsersViewModel
import io.github.null2264.githubuser.databinding.ActivityMainBinding
import io.github.null2264.githubuser.lib.Token
import io.github.null2264.githubuser.ui.auth.AuthActivity

class MainActivity : AppCompatActivity(), UsersRecyclerInterface {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: MainUsersViewModel
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.requestFocus()

        sharedPref = getSharedPreferences("GITHUB_TOKEN", MODE_PRIVATE)
        if (Token.fromSharedPreference(sharedPref) == null && !started)
            startActivity(Intent(this, AuthActivity::class.java))
        else
            actuallyStart()
    }

    override fun onRestart() {
        super.onRestart()
        if (!started)
            actuallyStart()
    }

    override fun onStop() {
        super.onStop()
        if (started)
            binding.svMain.setQuery("", false)
    }

    private fun actuallyStart() {
        setSupportActionBar(binding.mainToolbar)

        binding.rvUsers.setHasFixedSize(true)

        val factory = TokenViewModelFactory(Token.fromSharedPreference(sharedPref)!!.token)
        viewModel = ViewModelProvider(this, factory)[MainUsersViewModel::class.java]
        viewModel.apply {
            showRecyclerList(
                applicationContext,
                this@MainActivity,
                binding.rvUsers,
                users
            )

            users.observe(this@MainActivity) {
                if (it.isEmpty())
                    binding.tvMainInfo.text = buildString {
                        append(getString(R.string.no_users_prefix))
                        append(" '${getCurrentQuery()}'")
                    }
                binding.tvMainInfo.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            }

            isLoading.observe(this@MainActivity) {
                binding.apply {
                    if (refreshMain.isRefreshing)
                        refreshMain.isRefreshing = it
                    else
                        if (it == true) {
                            pbMainLoading.visibility = View.VISIBLE
                            tvMainError.visibility = View.GONE
                            tvMainInfo.visibility = View.GONE
                        } else
                            pbMainLoading.visibility = View.GONE
                }
            }

            error.observe(this@MainActivity) {
                binding.apply {
                    if (it != null) {
                        tvMainError.apply {
                            visibility = View.VISIBLE
                            text = StringBuilder("ERROR: ").append(getString(it))
                        }
                        rvUsers.visibility = View.GONE
                    } else {
                        tvMainError.apply {
                            visibility = View.GONE
                        }
                        rvUsers.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.apply {
            refreshMain.setOnRefreshListener {
                viewModel.getUsers()
            }

            svMain.setIconifiedByDefault(false)
            // Stupid "bug" or behaviour where SearchView won't clear focus with just clearFocus()
            svMain.isFocusedByDefault = false
            svMain.focusable = View.NOT_FOCUSABLE
            svMain.clearFocus()

            svMain.setOnQueryTextFocusChangeListener { _, hasFocus ->
                val marginEnd = if (hasFocus) 18 else 10
                val params = svMain.layoutParams as MarginLayoutParams
                params.updateMarginsRelative(end = marginEnd.px)
                svMain.layoutParams = params
                mainToolbar.menu.setGroupVisible(R.id.main_menu_group, !hasFocus)
            }
            svMain.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query == null)
                        return false
                    viewModel.getUsers(query)
                    svMain.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }

        started = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}