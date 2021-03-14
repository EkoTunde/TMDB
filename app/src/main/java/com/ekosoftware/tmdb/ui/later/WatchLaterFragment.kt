package com.ekosoftware.tmdb.ui.later

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.databinding.FragmentWatchLaterBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.ui.adapter.MoviesLoadStateAdapter
import com.ekosoftware.tmdb.ui.adapter.MoviesPagerAdapter
import com.ekosoftware.tmdb.ui.movies.MoviesFragmentDirections
import com.ekosoftware.tmdb.util.hide
import com.ekosoftware.tmdb.util.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchLaterFragment : Fragment(R.layout.fragment_watch_later) {
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private val moviesAdapter = MoviesPagerAdapter { movie, imageView ->
        val action = MoviesFragmentDirections.actionHomeFragmentToDetailFragment(movie.id)
        val extras = FragmentNavigatorExtras(
            imageView to (movie.posterPath ?: "")
        )
        findNavController().navigate(action, extras)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWatchLaterBinding.bind(view)
        initRecyclerView()
        initLoadStateListener()
        fetchData()
    }

    private fun initRecyclerView() = binding.apply {
        recyclerView.adapter = moviesAdapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { moviesAdapter.retry() },
            footer = MoviesLoadStateAdapter { moviesAdapter.retry() }
        )
        buttonRetry.setOnClickListener {
            moviesAdapter.retry()
        }
    }

    private fun initLoadStateListener() = moviesAdapter.addLoadStateListener { loadState ->
        binding.apply {
            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
            textViewError.isVisible = loadState.source.refresh is LoadState.Error

            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && moviesAdapter.itemCount < 1) {
                recyclerView.isVisible = false
                textViewEmpty.isVisible = true
            } else {
                textViewEmpty.isVisible = false
            }
        }
    }

    private fun fetchData() = viewModel.getWatchLater().observe(viewLifecycleOwner) {

    }


    private fun showProgress() = binding.run {
        progressBar.show()
        recyclerView.hide()
    }

    private fun hideProgress() = binding.run {
        progressBar.hide()
        recyclerView.show()
    }
}