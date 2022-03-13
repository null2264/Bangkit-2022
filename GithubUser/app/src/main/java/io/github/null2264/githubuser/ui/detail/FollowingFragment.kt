package io.github.null2264.githubuser.ui.detail

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.detail.DetailViewModel
import io.github.null2264.githubuser.databinding.FragmentFollowingBinding
import io.github.null2264.githubuser.lib.User

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
            following.observe(this@FollowingFragment) {
                if (it.isNotEmpty())
                    showRecyclerList(it)
            }
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

    private fun showRecyclerList(users: List<User>) {
        if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            binding.rvFollowing.layoutManager = GridLayoutManager(context, 2)
        else
            binding.rvFollowing.layoutManager = LinearLayoutManager(context)

        val mainUsersAdapter = DetailFollowsAdapter(users)
        binding.rvFollowing.adapter = mainUsersAdapter

        mainUsersAdapter.setOnItemClickCallback(object : DetailFollowsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                startActivity(Intent(context, DetailActivity::class.java)
                    .putExtra(DetailActivity.DATA, data))
            }
        })
    }
}