package io.github.null2264.dicodingstories.ui.story

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.Fade.OUT
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.other.LoadingStateAdapter
import io.github.null2264.dicodingstories.data.preference.PreferencesHelper
import io.github.null2264.dicodingstories.data.story.StoriesRecyclerAdapter
import io.github.null2264.dicodingstories.data.story.StoryViewModel
import io.github.null2264.dicodingstories.databinding.FragmentDashboardBinding
import io.github.null2264.dicodingstories.widget.dialog.ZiAlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val binding: FragmentDashboardBinding by viewBinding(CreateMethod.INFLATE)
    private val viewModel: StoryViewModel by viewModels()
    private val storiesAdapter by lazy { StoriesRecyclerAdapter(requireContext()) }

    @Inject
    lateinit var prefs: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedElementReturnTransition = Fade(OUT)
        returnTransition = Fade(OUT)
        binding.apply {
            storiesAdapter.addLoadStateListener {
                rvStoriesContainer.isVisible = it.refresh !is LoadState.Loading || it.refresh !is LoadState.Error
                if (!swipeRefresh.isRefreshing)
                    pbLoading.isVisible = it.refresh is LoadState.Loading
                else
                    swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
            rvStoriesContainer.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = storiesAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storiesAdapter.retry()
                    }
                )
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
        val token = runBlocking(Dispatchers.IO) { prefs.getToken().first() }
        val navController = findNavController()

        if (token == "") {
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
                getState().observe(this@DashboardFragment) {
                    binding.btnMap.isVisible = it.locationOnly
                }

                stories.observe(this@DashboardFragment) {
                    // TODO - Known bug: Loading/Error won't show up
                    storiesAdapter.submitData(lifecycle, it)
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
                    content = getString(R.string.logout_prompt)
                    isCancelEnabled = true
                    setOnConfirmListener {
                        runBlocking(Dispatchers.IO) { prefs.setToken("") }
                        goToLogin()
                    }
                }.show()
                true
            }
            else -> {super.onOptionsItemSelected(item)}
        }
    }
}