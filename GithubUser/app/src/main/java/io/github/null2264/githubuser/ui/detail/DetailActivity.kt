package io.github.null2264.githubuser.ui.detail

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.database.entity.UserEntity
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.data.detail.DetailViewModelFactory
import io.github.null2264.githubuser.data.preference.SettingPreferences
import io.github.null2264.githubuser.databinding.ActivityDetailBinding
import io.github.null2264.githubuser.lib.ActivityInjection
import io.github.null2264.githubuser.lib.Common
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(R.layout.activity_detail) {
    private lateinit var viewModel: DetailViewModel
    private var isUserFavorited by Delegates.notNull<Boolean>()
    private val binding: ActivityDetailBinding by viewBinding(CreateMethod.INFLATE)
    lateinit var user: UserEntity

    @Inject
    lateinit var prefs: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = intent.getParcelableExtra("USER_DATA")!!
        bind(user)

        lifecycleScope.launch {
            val factory = DetailViewModelFactory(ActivityInjection.provideRepository(
                applicationContext, prefs.getToken().first()!!), user)
            viewModel = ViewModelProvider(this@DetailActivity, factory)[DetailViewModel::class.java]
        }

        val detailPagerAdapter = DetailPagerAdapter(this)
        binding.apply {
            detailPager.adapter = detailPagerAdapter

            TabLayoutMediator(detailTabs, detailPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()

            detailAppbar.elevation = 0f

            fabFavorite.setOnClickListener { viewModel.toggleFavorite(isUserFavorited) }
        }

        viewModel.apply {
            user.observe(this@DetailActivity) {
                bind(it)
            }

            isFavorite(this@DetailActivity.user.id).observe(this@DetailActivity) {
                val notFilled = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_heart)
                val filled = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_heart_fill)
                binding.apply {
                    if (filled != null && notFilled != null)
                        when (it) {
                            true -> fabFavorite.setImageDrawable(filled)
                            false -> fabFavorite.setImageDrawable(notFilled)
                        }
                    isUserFavorited = it
                }
            }
        }
    }

    private fun bind(user: UserEntity) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .asBitmap()
                .load(user.avatarUrl)
                .circleCrop()
                .into(ivDetailAvatar)

            ivDetailAvatar.setOnClickListener {
                // TODO: Improve dialog appearance
                val dialog = Dialog(this@DetailActivity)
                dialog.apply {
                    setContentView(R.layout.avatar_dialog)
                    setCanceledOnTouchOutside(true)

                    window?.setBackgroundDrawableResource(android.R.color.transparent)

                    val dialogImageView: ImageView = findViewById(R.id.iv_dialog_avatar)
                    dialogImageView.setOnClickListener { hide() }

                    Glide.with(context)
                        .asBitmap()
                        .load(user.avatarUrl)
                        .into(dialogImageView)

                    show()
                }
            }

            if (user.name == null) {
                tvDetailName.text = user.login
                tvDetailUsername.visibility = View.GONE
            } else {
                tvDetailName.text = user.name
                tvDetailUsername.text = user.login
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
                putExtra(Intent.EXTRA_TEXT, Common.GITHUB_BASE_URL + user.login)
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