package com.ekosoftware.tmdb.ui.home.watchlater

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.Strings
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.databinding.FragmentMoviesBinding
import com.ekosoftware.tmdb.databinding.FragmentWatchLaterBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.ui.adapters.WatchLaterListAdapter
import com.ekosoftware.tmdb.ui.home.movies.MoviesFragmentDirections
import com.ekosoftware.tmdb.util.hide
import com.ekosoftware.tmdb.util.show
import com.ekosoftware.tmdb.util.snack
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchLaterFragment : Fragment() {
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private val watchLaterListAdapter = WatchLaterListAdapter { movie, cardView ->
        navigateToDetail(movie.id, cardView)
    }

    private fun navigateToDetail(movieId: Long, cardView: MaterialCardView) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        val action = WatchLaterFragmentDirections.actionWatchLaterFragmentToDetailFragment(movieId)
        val extras = FragmentNavigatorExtras(
            cardView to Strings.get(
                R.string.movie_card_detail_transition_name,
                movieId
            )
        )
        findNavController().navigate(action, extras)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        binding.toolbar.inflateMenu(R.menu.search_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_search -> {
                    val directions = WatchLaterFragmentDirections.actionWatchLaterPageFragmentToSearchFragment()
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
        fetchData()
        initRecyclerView()
    }

    private fun initRecyclerView() = binding.recyclerView.apply {
        adapter = watchLaterListAdapter
    }

    private fun fetchData() = viewModel.getWatchLater().observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Loading -> showProgress()
            is Resource.Success -> {
                hideProgress()
                watchLaterListAdapter.submitList(result.data)
                binding.textViewEmpty.isVisible = result.data.isNullOrEmpty()
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