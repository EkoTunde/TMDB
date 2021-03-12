package com.ekosoftware.tmdb.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.databinding.FragmentMoviesBinding
import com.ekosoftware.tmdb.presentation.MoviesViewModel
import com.ekosoftware.tmdb.ui.adapter.MoviesPagerAdapter
import com.ekosoftware.tmdb.ui.adapter.MoviesLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : Fragment(R.layout.fragment_movies) {

    companion object {
        private const val TAG = "MoviesFragment"
    }

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MoviesViewModel>()

    private val moviesPagerAdapter = MoviesPagerAdapter { movie ->
        val action = MoviesFragmentDirections.actionHomeFragmentToDetailFragment(movie.id)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMoviesBinding.bind(view)

        binding.apply {
            recyclerView.adapter = moviesPagerAdapter.withLoadStateHeaderAndFooter(
                header = MoviesLoadStateAdapter { moviesPagerAdapter.retry() },
                footer = MoviesLoadStateAdapter { moviesPagerAdapter.retry() }
            )
            buttonRetry.setOnClickListener {
                moviesPagerAdapter.retry()
            }
            chipGroup.setOnCheckedChangeListener { _, checkedId ->

                val id = when (checkedId) {
                    R.id.chip_now_playing -> MoviesViewModel.TYPE_NOW_PLAYING
                    R.id.chip_popular -> MoviesViewModel.TYPE_POPULAR
                    R.id.chip_top_rated -> MoviesViewModel.TYPE_TOP_RATED
                    R.id.chip_upcoming -> MoviesViewModel.TYPE_UPCOMING
                    else -> 1
                }
            }
        }

        moviesPagerAdapter.addLoadStateListener { loadState ->
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

        viewModel.movies.observe(viewLifecycleOwner) {
            moviesPagerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}