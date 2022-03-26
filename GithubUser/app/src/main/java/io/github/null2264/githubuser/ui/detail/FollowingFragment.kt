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
import io.github.null2264.githubuser.databinding.FragmentFollowingBinding
import io.github.null2264.githubuser.ui.base.BaseUsersRecyclerFragment

class FollowingFragment : BaseUsersRecyclerFragment(R.layout.fragment_following) {
    private val binding: FragmentFollowingBinding by viewBinding(CreateMethod.INFLATE)
    private val sharedViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvFollowing
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
                result.observe(this@FollowingFragment) {
                    when (it) {
                        is Result.Success -> {
                            swipeRefresh.isRefreshing = false
                            @Suppress("UNCHECKED_CAST")
                            adapter.submitList(it.data.second)
                            tvFollowingError.apply {
                                visibility = View.GONE
                            }
                            rvFollowing.visibility = View.VISIBLE
                        }
                        is Result.Loading -> swipeRefresh.isRefreshing = true
                        is Result.Error -> {
                            swipeRefresh.isRefreshing = false
                            binding.apply {
                                if (user.following <= 0)
                                    Log.d("FollowingFragment", "No following found, skipping error...")
                                tvFollowingError.apply {
                                    visibility = View.VISIBLE
                                    text = StringBuilder("ERROR: ").append(getString(it.stringRes))
                                }
                                rvFollowing.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun bind(user: UserEntity) {
        if (user.following < 1)
            binding.apply {
                tvFollowingInfo.text = buildString {
                    append(user.name ?: user.login)
                    append(" ")
                    append(getString(R.string.no_following_suffix))
                }
                tvFollowingInfo.visibility = View.VISIBLE
                swipeRefresh.isEnabled = false
            }
    }
}