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
import io.github.null2264.githubuser.databinding.FragmentFollowingBinding

class FollowingFragment : Fragment(R.layout.fragment_following), UsersRecyclerInterface {
    private val binding by viewBinding<FragmentFollowingBinding>(CreateMethod.INFLATE)
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
        if (user.following < 1) {
            binding.tvFollowingInfo.text = buildString {
                append(user.name ?: user.username)
                append(" ")
                append(getString(R.string.no_following_suffix))
            }
            binding.tvFollowingInfo.visibility = View.VISIBLE
        }

        sharedViewModel.apply {
            showRecyclerList(
                context,
                this@FollowingFragment,
                binding.rvFollowing,
                following
            )

            isLoading.observe(this@FollowingFragment) {
                binding.refreshFollowing.isRefreshing = it
            }
            error.observe(this@FollowingFragment) {
                binding.apply {
                    if (user.following <= 0) {
                        Log.d("FollowingFragment", "No following found, skipping error...")
                    } else if (it != null) {
                        tvFollowingError.apply {
                            visibility = View.VISIBLE
                            text = StringBuilder("ERROR: ").append(getString(it))
                        }
                        rvFollowing.visibility = View.GONE
                    } else {
                        tvFollowingError.apply {
                            visibility = View.GONE
                        }
                        rvFollowing.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.refreshFollowing.apply {
            setOnRefreshListener {
                if (user.following >= 1)
                    sharedViewModel.getFollows()
                else
                    this.isRefreshing = false
            }
        }
    }
}