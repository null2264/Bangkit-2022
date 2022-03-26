package io.github.null2264.githubuser.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.common.Result
import io.github.null2264.githubuser.data.main.MainUsersViewModel
import io.github.null2264.githubuser.databinding.FragmentMainExploreBinding
import io.github.null2264.githubuser.ui.base.BaseUsersRecyclerFragment

class MainExploreFragment : BaseUsersRecyclerFragment(R.layout.fragment_main_explore) {
    private val viewModel: MainUsersViewModel by activityViewModels()
    private val binding: FragmentMainExploreBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.apply {
            recyclerView = rvUsers
            setupRecyclerList()

            swipeRefresh.apply {
                isEnabled = false
                setOnRefreshListener {
                    viewModel.refreshSearch()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<SearchView>(R.id.sv_main)?.apply {
            setQuery(viewModel.getSearchQuery() ?: "", false)
            clearFocus()
        }

        viewModel.apply {
            searchResult.observe(this@MainExploreFragment) {
                when (it) {
                    is Result.Loading -> {
                        binding.apply {
                            if (!swipeRefresh.isRefreshing) {
                                swipeRefresh.isEnabled = false
                                pbLoading.visibility = View.VISIBLE
                                tvMainError.visibility = View.GONE
                                tvMainInfo.visibility = View.GONE
                                rvUsers.visibility = View.GONE
                            }
                        }
                    }

                    is Result.Success -> {
                        binding.apply {
                            swipeRefresh.isRefreshing = false
                            pbLoading.visibility = View.GONE
                            if (it.data.isEmpty()) {
                                tvMainInfo.apply {
                                    text = getString(R.string.greeting)
                                    visibility = View.VISIBLE
                                }
                                rvUsers.visibility = View.GONE
                            } else {
                                adapter.submitList(it.data)
                                rvUsers.visibility = View.VISIBLE
                            }
                            swipeRefresh.isEnabled = it.data.isNotEmpty()
                            tvMainInfo.visibility = if (it.data.isNotEmpty()) View.GONE else View.VISIBLE
                        }
                    }

                    is Result.Error -> {
                        binding.apply {
                            swipeRefresh.apply {
                                isRefreshing = false
                                isEnabled = false
                            }
                            pbLoading.visibility = View.GONE
                            if (it.value != null)
                                tvMainInfo.apply {
                                    text = buildString {
                                        append(getString(it.stringRes))
                                        append(it.value)
                                    }
                                    visibility = View.VISIBLE
                                }
                            else
                                tvMainError.apply {
                                    visibility = View.VISIBLE
                                    text = StringBuilder("ERROR: ").append(getString(it.stringRes))
                                }
                            rvUsers.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}