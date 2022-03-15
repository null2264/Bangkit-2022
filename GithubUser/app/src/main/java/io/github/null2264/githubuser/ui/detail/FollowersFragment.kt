package io.github.null2264.githubuser.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.databinding.FragmentFollowersBinding

class FollowersFragment : Fragment() {
    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<DetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFollowersBinding.inflate(inflater, container, false)
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
                DetailActivity::class.java,
                this@FollowersFragment,
                binding.rvFollowers,
                followers
            )

            isLoading.observe(this@FollowersFragment) {
                binding.refreshFollowers.isRefreshing = it
            }
            error.observe(this@FollowersFragment) {
                binding.apply {
                    if (it != null) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}