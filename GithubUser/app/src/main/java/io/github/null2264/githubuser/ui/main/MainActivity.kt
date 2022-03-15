package io.github.null2264.githubuser.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.TokenViewModelFactory
import io.github.null2264.githubuser.data.main.MainUsersViewModel
import io.github.null2264.githubuser.databinding.ActivityMainBinding
import io.github.null2264.githubuser.lib.Token
import io.github.null2264.githubuser.lib.User
import io.github.null2264.githubuser.ui.auth.AuthActivity
import io.github.null2264.githubuser.ui.detail.DetailActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var searchMenuItem: MenuItem
    private lateinit var viewModel: MainUsersViewModel
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            searchMenuItem.collapseActionView()
    }

    private fun actuallyStart() {
        setSupportActionBar(binding.mainToolbar)

        binding.rvUsers.setHasFixedSize(true)

        val factory = TokenViewModelFactory(Token.fromSharedPreference(sharedPref)!!.token)
        viewModel = ViewModelProvider(this, factory)[MainUsersViewModel::class.java]
        viewModel.apply {
            showRecyclerList(users)
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

    private fun showRecyclerList(users: LiveData<List<User>>) {
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        else
            binding.rvUsers.layoutManager = LinearLayoutManager(this)

        val mainUsersAdapter = MainUsersAdapter()
        binding.rvUsers.adapter = mainUsersAdapter

        users.observe(this@MainActivity) { newList ->
            mainUsersAdapter.setList(newList)
        }

        mainUsersAdapter.setOnItemClickCallback(object : MainUsersAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                startActivity(Intent(this@MainActivity, DetailActivity::class.java)
                    .putExtra("USER_DATA", data))
            }
        })
    }
}