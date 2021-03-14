package com.ekosoftware.tmdb.ui.watchlater

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.Strings
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.databinding.FragmentWatchLaterBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.ui.adapter.MoviesListAdapter
import com.ekosoftware.tmdb.ui.adapter.MoviesLoadStateAdapter
import com.ekosoftware.tmdb.ui.adapter.MoviesPagerAdapter
import com.ekosoftware.tmdb.ui.movies.MoviesFragmentDirections
import com.ekosoftware.tmdb.util.hide
import com.ekosoftware.tmdb.util.show
import com.ekosoftware.tmdb.util.snack
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchLaterFragment : Fragment(R.layout.fragment_watch_later) {
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private val moviesAdapter = MoviesListAdapter { movie, cardView ->
        navigateToDetail(movie.id, cardView)
    }

    private fun navigateToDetail(movieId: Long, cardView: CardView) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        val action = WatchLaterFragmentDirections.actionWatchLaterFragmentToDetailFragment(movieId)
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
        _binding = FragmentWatchLaterBinding.bind(view)
        initRecyclerView()
        fetchData()
    }

    private fun initRecyclerView() = binding.apply {
        recyclerView.adapter = moviesAdapter
    }


    private fun fetchData() = viewModel.getWatchLater().observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> showProgress()
            is Resource.Success -> {
                hideProgress()
                binding.textViewEmpty.isVisible = result.data.isNullOrEmpty()
                moviesAdapter.submitList(result.data)
            }
            is Resource.Error -> {
                hideProgress()
                binding.snack(result.message ?: "")
            }
        }
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