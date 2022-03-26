package io.github.null2264.githubuser.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.common.Result
import io.github.null2264.githubuser.data.database.entity.UserEntity
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.databinding.FragmentFollowersBinding
import io.github.null2264.githubuser.ui.base.BaseUsersRecyclerFragment

class FollowersFragment : BaseUsersRecyclerFragment(R.layout.fragment_followers) {
    private val binding: FragmentFollowersBinding by viewBinding(CreateMethod.INFLATE)
    private val sharedViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvFollowers
        val user = (activity as DetailActivity).user
        bind(user)
        setupRecyclerList()

        binding.swipeRefresh.apply {
            setOnRefreshListener {
                sharedViewModel.getFollows()
            }
        }

        sharedViewModel.apply {
            binding.apply {
                result.observe(this@FollowersFragment) {
                    when (it) {
                        is Result.Success -> {
                            swipeRefresh.isRefreshing = false
                            @Suppress("UNCHECKED_CAST")
                            adapter.submitList(it.data.first)
                            tvFollowersError.apply {
                                visibility = View.GONE
                            }
                            rvFollowers.visibility = View.VISIBLE
                        }
                        is Result.Loading -> swipeRefresh.isRefreshing = true
                        is Result.Error -> {
                            swipeRefresh.isRefreshing = false
                            binding.apply {
                                if (user.followers <= 0)
                                    Log.d("FollowersFragment", "No followers found, skipping error...")
                                tvFollowersError.apply {
                                    visibility = View.VISIBLE
                                    text = StringBuilder("ERROR: ").append(getString(it.stringRes))
                                }
                                rvFollowers.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun bind(user: UserEntity) {
        if (user.followers < 1)
            binding.apply {
                tvFollowersInfo.text = buildString {
                    append(user.name ?: user.login)
                    append(" ")
                    append(getString(R.string.no_followers_suffix))
                }
                tvFollowersInfo.visibility = View.VISIBLE
                swipeRefresh.isEnabled = false
            }
    }
}