package io.github.null2264.dicodingstories.ui.story

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Explode
import androidx.transition.Fade
import androidx.transition.Fade.OUT
import androidx.transition.TransitionInflater
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.preference.PreferencesHelper
import io.github.null2264.dicodingstories.data.story.StoriesRecyclerAdapter
import io.github.null2264.dicodingstories.data.story.StoryViewModel
import io.github.null2264.dicodingstories.databinding.FragmentDashboardBinding
import io.github.null2264.dicodingstories.lib.Result
import io.github.null2264.dicodingstories.widget.dialog.ZiAlertDialog
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val binding: FragmentDashboardBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: StoryViewModel by viewModels()
    private val adapter by lazy { StoriesRecyclerAdapter(this@DashboardFragment) }

    @Inject
    lateinit var prefs: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedElementReturnTransition = Fade(OUT)
        returnTransition = Fade(OUT)
        binding.apply {
            rvStoriesContainer.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = this@DashboardFragment.adapter
            }
            setHasOptionsMenu(true)
            (requireActivity() as AppCompatActivity).setSupportActionBar(appbar)
        }

        return binding.root
    }

    private fun goToLogin() {
        val navController = findNavController()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(navController.graph.startDestinationId, true)
            .build()
        navController.navigate(R.id.loginFragment, null, navOptions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val token = prefs.getToken()
        val navController = findNavController()

        if (token == null) {
            goToLogin()
        }

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>("isSuccess")?.observe(viewLifecycleOwner) {
            if (it != null) {
                savedStateHandle.remove<Boolean>("isSuccess")
                if (it == true) {
                    binding.swipeRefresh.isRefreshing = true
                    viewModel.refreshStories()
                }
            }
        }

        binding.apply {
            btnNewStory.setOnClickListener { navController.navigate(R.id.action_add_new_story) }

            swipeRefresh.setOnRefreshListener { viewModel.refreshStories() }

            viewModel.apply {
                stories.observe(this@DashboardFragment) {
                    when (it) {
                        is Result.Loading -> {
                            if (!swipeRefresh.isRefreshing)
                                pbLoading.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            adapter.submitList(it.data)
                        }
                        is Result.Error -> {
                            tvError.text = getString(it.stringId)
                        }
                    }

                    if (it !is Result.Loading) {
                        if (!swipeRefresh.isRefreshing)
                            pbLoading.visibility = View.GONE
                        else
                            swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_logout -> {
                ZiAlertDialog(requireContext()).apply {
                    type = ZiAlertDialog.WARNING
                    title = getString(R.string.logout)
                    content = "Are you sure?"
                    isCancelEnabled = true
                    setOnConfirmListener {
                        prefs.setToken(null)
                        goToLogin()
                    }
                }.show()
                true
            } // logout
            else -> {super.onOptionsItemSelected(item)}
        }
    }
}