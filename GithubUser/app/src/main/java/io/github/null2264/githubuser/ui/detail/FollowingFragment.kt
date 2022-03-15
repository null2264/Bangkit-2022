package io.github.null2264.githubuser.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.databinding.FragmentFollowingBinding

class FollowingFragment : Fragment() {
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
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
                DetailActivity::class.java,
                this@FollowingFragment,
                binding.rvFollowing,
                following
            )

            isLoading.observe(this@FollowingFragment) {
                binding.refreshFollowing.isRefreshing = it
            }
            error.observe(this@FollowingFragment) {
                binding.apply {
                    if (it != null) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}