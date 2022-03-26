package io.github.null2264.githubuser.ui.main

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.common.Result
import io.github.null2264.githubuser.data.main.MainUsersViewModel
import io.github.null2264.githubuser.databinding.FragmentMainFavoritesBinding
import io.github.null2264.githubuser.ui.base.BaseUsersRecyclerFragment

class MainFavoritesFragment : BaseUsersRecyclerFragment(R.layout.fragment_main_favorites) {
    private val viewModel: MainUsersViewModel by activityViewModels()
    private val binding: FragmentMainFavoritesBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.apply {
            recyclerView = rvUsers
            setupRecyclerList()
            showGreeting()

            swipeRefresh.apply {
                isEnabled = false
                setOnRefreshListener {
                    viewModel.refreshFavorite()
                }
            }
        }

        return binding.root
    }

    private fun showGreeting() {
        binding.apply {
            context.apply {
                if (this != null) {
                    val drawableId = R.drawable.ic_compass
                    var color = android.R.attr.textColor

                    val a = TypedValue()
                    theme.resolveAttribute(color, a, true)
                    if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT)
                        color = a.data

                    val drawable = AppCompatResources.getDrawable(this, drawableId)?.mutate()
                    drawable?.apply {
                        setTint(color)
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                    }
                    val imageSpan = if (drawable != null) ImageSpan(drawable) else ImageSpan(this, drawableId)

                    val prefix = getString(R.string.greeting_favorites_prefix) + " *"
                    val spanText = SpannableStringBuilder(prefix).apply {
                        setSpan(imageSpan, prefix.length - 1, prefix.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        append(" ").append(getString(R.string.greeting_favorites_suffix))
                    }
                    tvMainInfo.text = spanText
                    tvMainInfo.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<SearchView>(R.id.sv_main)?.apply {
            setQuery(viewModel.getFavoriteQuery() ?: "", false)
            clearFocus()
        }

        viewModel.apply {
            favorites.observe(this@MainFavoritesFragment) {
                when (it) {
                    is Result.Loading -> {
                        binding.apply {
                            if (!swipeRefresh.isRefreshing) {
                                swipeRefresh.isEnabled = false
                                pbLoading.visibility = View.VISIBLE
                                tvMainError.visibility = View.GONE
                                tvMainInfo.visibility = View.GONE
                            }
                        }
                    }

                    is Result.Success -> {
                        adapter.submitList(it.data)
                        binding.apply {
                            swipeRefresh.isRefreshing = false
                            swipeRefresh.isEnabled = true

                            pbLoading.visibility = View.GONE
                            tvMainInfo.visibility = View.GONE

                            rvUsers.visibility = View.VISIBLE
                        }
                    }

                    is Result.Error -> {
                        binding.apply {
                            swipeRefresh.apply {
                                isRefreshing = false
                                isEnabled = false
                            }
                            pbLoading.visibility = View.GONE
                            tvMainError.apply {
                                if (it.value != null) {
                                    text = StringBuilder(getString(it.stringRes)).append(" ${it.value}")
                                    visibility = View.VISIBLE
                                    tvMainInfo.visibility = View.GONE
                                } else if (it.stringRes != 0) {
                                    text = StringBuilder("ERROR: ").append(getString(it.stringRes))
                                    visibility = View.VISIBLE
                                    tvMainInfo.visibility = View.GONE
                                } else {
                                    showGreeting()
                                    visibility = View.GONE
                                }
                            }
                            rvUsers.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}