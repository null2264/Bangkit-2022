package io.github.null2264.githubuser.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.main.MainUsersViewModel
import io.github.null2264.githubuser.databinding.ActivityMainBinding
import io.github.null2264.githubuser.lib.User
import io.github.null2264.githubuser.lib.getToken
import io.github.null2264.githubuser.ui.auth.AuthActivity
import io.github.null2264.githubuser.ui.detail.DetailActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var searchMenuItem: MenuItem
    private var started = false
    private val viewModel by viewModels<MainUsersViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: Split to 2 fragment, home fragment and explore fragment
        // TODO: Use navigation library
        // TODO: Home/Front page = Viewer's profile maybe?
        installSplashScreen()
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("GITHUB_TOKEN", MODE_PRIVATE)
        if (getToken(sharedPref) == null)
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
            searchMenuItem.collapseActionView()
    }

    private fun actuallyStart() {
        setSupportActionBar(binding.mainToolbar)

        binding.rvUsers.setHasFixedSize(true)

        viewModel.apply {
            // Init
            setToken(getToken(sharedPref)!!)
            getUsers()

            users.observe(this@MainActivity) {
                showRecyclerList(it)
            }

            isLoading.observe(this@MainActivity) {
                binding.apply {
                    if (refreshMain.isRefreshing)
                        refreshMain.isRefreshing = it
                    else
                        pbMainLoading.visibility = if (it == false) View.GONE else View.VISIBLE
                }
            }

            error.observe(this@MainActivity) {
                binding.apply {
                    if (it != null)
                        tvMainError.apply {
                            visibility = View.VISIBLE
                            text = StringBuilder("ERROR: ").append(getString(it))
                        }
                    else
                        tvMainError.apply {
                            visibility = View.GONE
                        }
                }
            }
        }

        binding.refreshMain.setOnRefreshListener {
            viewModel.getUsers()
        }

        started = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        searchMenuItem = menu.findItem(R.id.main_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null)
                    return false
                viewModel.getUsers(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    private fun showRecyclerList(users: List<User>) {
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        else
            binding.rvUsers.layoutManager = LinearLayoutManager(this)

        val mainUsersAdapter = MainUsersAdapter(users)
        binding.rvUsers.adapter = mainUsersAdapter

        mainUsersAdapter.setOnItemClickCallback(object : MainUsersAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                startActivity(Intent(this@MainActivity, DetailActivity::class.java)
                    .putExtra(DetailActivity.DATA, data))
            }
        })
    }
}