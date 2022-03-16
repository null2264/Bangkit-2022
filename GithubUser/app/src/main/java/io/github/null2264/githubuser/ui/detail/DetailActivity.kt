package io.github.null2264.githubuser.ui.detail

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.data.detail.DetailViewModelFactory
import io.github.null2264.githubuser.databinding.ActivityDetailBinding
import io.github.null2264.githubuser.lib.Common
import io.github.null2264.githubuser.lib.Token
import io.github.null2264.githubuser.lib.User

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: DetailViewModel
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = intent.getParcelableExtra("USER_DATA")!!

        Glide.with(this)
            .asBitmap()
            .load(user.avatar)
            .circleCrop()
            .into(binding.ivDetailAvatar)

        binding.apply {
            ivDetailAvatar.setOnClickListener {
                val dialog = Dialog(this@DetailActivity)
                dialog.setContentView(R.layout.avatar_dialog)

                Glide.with(dialog.context)
                    .asBitmap()
                    .load(user.avatar)
                    .circleCrop()
                    .into(dialog.findViewById(R.id.iv_dialog_avatar))

                dialog.show()
            }

            if (user.name == null) {
                tvDetailName.text = user.username
                tvDetailUsername.visibility = View.GONE
            } else {
                tvDetailName.text = user.name
                tvDetailUsername.text = user.username
            }

            tvDetailRepository.text = user.repository.toString()
            tvDetailFollower.text = StringBuilder(user.followers.toString())
                .append(" followers")
            tvDetailFollowing.text = StringBuilder(user.following.toString())
                .append(" following")

            if (user.company == null)
                tvDetailCompany.visibility = View.GONE
            else
                tvDetailCompany.text = user.company

            if (user.location == null) {
                tvDetailLocation.visibility = View.GONE
            } else
                tvDetailLocation.text = user.location
        }

        val detailPagerAdapter = DetailPagerAdapter(this)
        binding.apply {
            detailPager.adapter = detailPagerAdapter

            TabLayoutMediator(detailTabs, detailPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()

            detailAppbar.elevation = 0f
        }

        sharedPref = getSharedPreferences("GITHUB_TOKEN", MODE_PRIVATE)

        val factory = DetailViewModelFactory(Token.fromSharedPreference(sharedPref)!!.token, user)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.share -> {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Common.GITHUB_BASE_URL + user.username)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, null))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.detail_tab_followers,
            R.string.detail_tab_following
        )
    }
}