package com.ekosoftware.tmdb.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.Strings
import com.ekosoftware.tmdb.databinding.FragmentSearchBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.ui.adapters.MoviesLoadStateAdapter
import com.ekosoftware.tmdb.ui.adapters.MoviesPagingAdapter
import com.ekosoftware.tmdb.util.showKeyboard
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private val moviesPagingAdapter: MoviesPagingAdapter =
        MoviesPagingAdapter { movie, materialCardView -> navigateTo(movie.id, materialCardView) }

    private fun navigateTo(movieId: Long, materialCardView: MaterialCardView) {
        val emailCardDetailTransitionName = Strings.get(R.string.movie_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(materialCardView to emailCardDetailTransitionName)
        val directions = SearchFragmentDirections.actionSearchFragmentToDetailFragment(movieId)
        findNavController().navigate(directions, extras)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.searchToolbar.setupWithNavController(navController, appBarConfiguration)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initPagingAdapter()
        fetchData()
        initSearchView()
        showKeyboard()
    }

    private fun initRecyclerView() = binding.recyclerView.apply {
        adapter = moviesPagingAdapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { moviesPagingAdapter.retry() },
            footer = MoviesLoadStateAdapter { moviesPagingAdapter.retry() }
        )
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

    private fun fetchData() = viewModel.searchedMovies.observe(viewLifecycleOwner) {
        if (it == null) {
            Toast.makeText(requireContext(), "Ingresa texto para buscar", Toast.LENGTH_SHORT).show()
        } else {
            moviesPagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun initSearchView() =
        binding.searchView.apply {
            requestFocus()
            showKeyboard()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.submitMovieQuery(it)
                    }
                    this@apply.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean = false
            })
        }

    override fun onDestroyView() {
        super.onDestroyView()
        //viewModel.submitMovieQuery(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}