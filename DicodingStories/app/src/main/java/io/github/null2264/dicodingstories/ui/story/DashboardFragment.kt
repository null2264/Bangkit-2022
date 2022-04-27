package io.github.null2264.dicodingstories.ui.story

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
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
import io.github.null2264.dicodingstories.data.story.StoryFilter
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
    private lateinit var loadStateListener: (CombinedLoadStates) -> Unit
    private var reload = false

    @Inject
    lateinit var prefs: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedElementReturnTransition = Fade(OUT)
        returnTransition = Fade(OUT)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val token = runBlocking(Dispatchers.IO) { prefs.getToken().first() }
        val navController = try {
            findNavController()
        } catch (_: IllegalStateException) {
            // probably testing
            null
        }

        if (token == "") {
            goToLogin()
        }

        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>("isSuccess")?.observe(viewLifecycleOwner) {
            if (it != null) {
                savedStateHandle.remove<Boolean>("isSuccess")
                if (it == true) {
                    binding.swipeRefresh.isRefreshing = true
                    storiesAdapter.refresh()
                }
            }
        }

        binding.apply {
            setHasOptionsMenu(true)
            (requireActivity() as AppCompatActivity).setSupportActionBar(appbar.toolbar)

            loadStateListener = {
                rvStoriesContainer.isVisible = it.refresh !is LoadState.Error

                if (it.refresh is LoadState.Error)
                    tvError.text = (it.refresh as LoadState.Error).error.localizedMessage ?: "Oops! Something went wrong."
                errorContainer.visibility = if (it.refresh is LoadState.Error) View.VISIBLE else View.GONE

                if (!reload)
                    reload = swipeRefresh.isRefreshing

                if (!swipeRefresh.isRefreshing)
                    loading.isVisible = it.refresh is LoadState.Loading
                else
                    swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }

            storiesAdapter.addLoadStateListener(loadStateListener)

            rvStoriesContainer.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = storiesAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storiesAdapter.retry()
                    }
                )
            }

            btnNewStory.setOnClickListener {
                navController?.navigate(R.id.action_add_new_story)
            }

            btnRetry.setOnClickListener {
                storiesAdapter.refresh()
            }
            swipeRefresh.setOnRefreshListener { storiesAdapter.refresh() }

            btnMap.setOnClickListener {
                val b = bundleOf("stories" to storiesAdapter.snapshot().items.toTypedArray())
                findNavController().navigate(R.id.action_dashboard_to_maps, b)
            }

            viewModel.apply {
                transitionState.observe(viewLifecycleOwner) { state ->
                    root.transitionToState(state, 1)
                }

                stories.observe(viewLifecycleOwner) {
                    storiesAdapter.submitData(lifecycle, it)
                    if (reload) {
                        rvStoriesContainer.scrollToPosition(0)
                        reload = false
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        storiesAdapter.removeLoadStateListener(loadStateListener)
    }

    private fun goToLogin() {
        val navController = findNavController()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(navController.graph.startDestinationId, true)
            .build()
        navController.navigate(R.id.loginFragment, null, navOptions)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_filter -> {
                val anchor = requireActivity().findViewById<View>(item.itemId)
                val popup = PopupMenu(requireContext(), anchor, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0)
                popup.menuInflater.inflate(R.menu.menu_filter, popup.menu)

                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_action_filter_location -> {
                            val current = viewModel.getFilterState().value ?: StoryFilter()

                            binding.apply {
                                swipeRefresh.isRefreshing = true
                                val state = if (!current.locationOnly) R.id.end else R.id.start
                                root.transitionToState(state)
                                viewModel.setTransitionState(state)
                            }

                            viewModel
                                .setFilterState(current.copy(locationOnly = !current.locationOnly))
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }

                popup.show()
                true
            }
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}