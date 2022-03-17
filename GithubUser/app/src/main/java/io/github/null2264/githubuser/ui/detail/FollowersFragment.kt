package io.github.null2264.githubuser.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.UsersRecyclerInterface
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.databinding.FragmentFollowersBinding

class FollowersFragment : Fragment(R.layout.fragment_followers), UsersRecyclerInterface {
    private val binding by viewBinding<FragmentFollowersBinding>(CreateMethod.INFLATE)
    private val sharedViewModel by activityViewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = (activity as DetailActivity).user
        if (user.followers < 1)
            binding.tvFollowersInfo.text = buildString {
                append(user.name ?: user.username)
                append(" ")
                append(getString(R.string.no_followers_suffix))
            }
        binding.tvFollowersInfo.visibility = View.VISIBLE

        sharedViewModel.apply {
            showRecyclerList(
                context,
                this@FollowersFragment,
                binding.rvFollowers,
                followers
            )

            isLoading.observe(this@FollowersFragment) {
                binding.refreshFollowers.isRefreshing = it
            }
            error.observe(this@FollowersFragment) {
                binding.apply {
                    if (user.followers <= 0) {
                        Log.d("FollowersFragment", "No followers found, skipping error...")
                    } else if (it != null) {
                        tvFollowersError.apply {
                            visibility = View.VISIBLE
                            text = StringBuilder("ERROR: ").append(getString(it))
                        }
                        rvFollowers.visibility = View.GONE
                    } else {
                        tvFollowersError.apply {
                            visibility = View.GONE
                        }
                        rvFollowers.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.refreshFollowers.apply {
            setOnRefreshListener {
                if (user.followers >= 1)
                    sharedViewModel.getFollows()
                else
                    this.isRefreshing = false
            }
        }
    }
}