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
import io.github.null2264.githubuser.databinding.FragmentFollowersBinding
import io.github.null2264.githubuser.lib.User

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
            followers.observe(this@FollowersFragment) {
                if (it.isNotEmpty())
                    showRecyclerList(it)
            }
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

    private fun showRecyclerList(users: List<User>) {
        if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            binding.rvFollowers.layoutManager = GridLayoutManager(context, 2)
        else
            binding.rvFollowers.layoutManager = LinearLayoutManager(context)

        val mainUsersAdapter = DetailFollowsAdapter(users)
        binding.rvFollowers.adapter = mainUsersAdapter

        mainUsersAdapter.setOnItemClickCallback(object : DetailFollowsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                startActivity(Intent(context, DetailActivity::class.java)
                    .putExtra(DetailActivity.DATA, data))
            }
        })
    }
}