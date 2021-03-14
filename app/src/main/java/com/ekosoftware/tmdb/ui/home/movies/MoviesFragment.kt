package com.ekosoftware.tmdb.ui.home.movies

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.Strings
import com.ekosoftware.tmdb.databinding.FragmentMoviesBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.ui.adapters.MoviesPagingAdapter
import com.ekosoftware.tmdb.ui.adapters.MoviesLoadStateAdapter
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    companion object {
        private const val TAG = "MoviesFragment"
    }

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private val moviesPagingAdapter = MoviesPagingAdapter { movie, materialCardView ->
        navigateToDetail(movie.id, materialCardView)
    }

    private fun navigateToDetail(movieId: Long, cardView: MaterialCardView) {
        /*exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }*/
        val emailCardDetailTransitionName = Strings.get(R.string.movie_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(cardView to emailCardDetailTransitionName)
        val directions = MoviesFragmentDirections.actionHomeFragmentToDetailFragment(movieId)
        findNavController().navigate(directions, extras)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        binding.searchToolbar.inflateMenu(R.menu.search_menu)
        binding.searchToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_search -> {
                    val directions =
                        MoviesFragmentDirections.actionMoviesPageFragmentToSearchFragment()
                    findNavController().navigate(directions)
                    true
                }
                else -> true
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initViews()
        initPagingAdapter()
        fetchData()
    }

    private fun initViews() = binding.apply {
        recyclerView.adapter = moviesPagingAdapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { moviesPagingAdapter.retry() },
            footer = MoviesLoadStateAdapter { moviesPagingAdapter.retry() }
        )
        buttonRetry.setOnClickListener {
            moviesPagingAdapter.retry()
        }
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val id = when (checkedId) {
                R.id.chip_now_playing -> MainViewModel.TYPE_NOW_PLAYING
                R.id.chip_popular -> MainViewModel.TYPE_POPULAR
                R.id.chip_top_rated -> MainViewModel.TYPE_TOP_RATED
                R.id.chip_upcoming -> MainViewModel.TYPE_UPCOMING
                else -> 1
            }
            viewModel.setQueryType(id)
        }
    }

    private fun initPagingAdapter() = moviesPagingAdapter.addLoadStateListener { loadState ->
        binding.apply {
            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
            textViewError.isVisible = loadState.source.refresh is LoadState.Error

            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && moviesPagingAdapter.itemCount < 1) {
                recyclerView.isVisible = false
                textViewEmpty.isVisible = true
            } else {
                textViewEmpty.isVisible = false
            }
        }
    }

    private fun fetchData() = viewModel.movies.observe(viewLifecycleOwner) {
        moviesPagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}