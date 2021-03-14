package com.ekosoftware.tmdb.ui.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.Strings
import com.ekosoftware.tmdb.databinding.FragmentMoviesBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.ui.adapter.MoviesPagerAdapter
import com.ekosoftware.tmdb.ui.adapter.MoviesLoadStateAdapter
import com.ekosoftware.tmdb.ui.details.DetailsFragmentArgs
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment(R.layout.fragment_movies) {

    companion object {
        private const val TAG = "MoviesFragment"
    }

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private val moviesPagerAdapter = MoviesPagerAdapter { movie, cardView ->
        navigateToDetail(movie.id, cardView)
    }

    private fun navigateToDetail(movieId: Long, cardView: CardView) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        val action = MoviesFragmentDirections.actionHomeFragmentToDetailFragment(movieId)
        val extras = FragmentNavigatorExtras(
            cardView to Strings.get(
                R.string.movement_card_detail_transition_name,
                movieId
            )
        )
        findNavController().navigate(action, extras)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMoviesBinding.bind(view)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initViews()
        initPagerAdapter()
        fetchData()
    }

    private fun initViews() = binding.apply {
        recyclerView.adapter = moviesPagerAdapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { moviesPagerAdapter.retry() },
            footer = MoviesLoadStateAdapter { moviesPagerAdapter.retry() }
        )
        buttonRetry.setOnClickListener {
            moviesPagerAdapter.retry()
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

    private fun initPagerAdapter() = moviesPagerAdapter.addLoadStateListener { loadState ->
        binding.apply {
            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
            textViewError.isVisible = loadState.source.refresh is LoadState.Error

            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && moviesPagerAdapter.itemCount < 1) {
                recyclerView.isVisible = false
                textViewEmpty.isVisible = true
            } else {
                textViewEmpty.isVisible = false
            }
        }
    }

    private fun fetchData() = viewModel.movies.observe(viewLifecycleOwner) {
        moviesPagerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}