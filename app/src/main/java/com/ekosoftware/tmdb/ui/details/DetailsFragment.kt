package com.ekosoftware.tmdb.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.GlideApp
import com.ekosoftware.tmdb.core.Resource
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.databinding.FragmentDetailsBinding
import com.ekosoftware.tmdb.presentation.MainViewModel
import com.ekosoftware.tmdb.util.asUrl
import com.ekosoftware.tmdb.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.view.*
import kotlinx.android.synthetic.main.loading_details.view.*

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading()
        initViews()
        fetchMovie()
    }

    private fun initViews() = binding.apply {
        arrayOf(
            motionLayout.navigationIcon,
            shimmerLayout.loading.navigationIcon,
            binding.defaultNavigationIcon
        ).forEach {
            it.setOnClickListener { navigateUp() }
        }
        motionLayout.floatingActionButton.setOnClickListener {
            viewModel.saveToWatchLater()
        }
    }

    private fun fetchMovie() =
        viewModel.getMovie().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> loading()
                is Resource.Success -> {
                    notLoading()
                    setData(result.data)
                }
                is Resource.Error -> {
                    error()
                    binding.errorText.text = result.message
                    setData(result.data)
                }
            }
        }

    private fun setData(movie: MovieEntity?) = movie?.let {
        binding.apply {
            GlideApp.with(requireContext())
                .load(it.backdropPath.asUrl())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error)
                .into(motionLayout.backdropImage)

            GlideApp.with(requireContext())
                .load(it.posterPath.asUrl())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error)
                .into(motionLayout.posterImage)

            motionLayout.title.text = if (it.title.isEmpty()) it.name else it.title
            motionLayout.genres.text = it.genres ?: ""

            nestedScrolling.rating.text = it.rating.toString()
            nestedScrolling.language.text = it.originalLanguage
            val revenue = "${(it.revenue ?: 0) / 1_000_000}M"
            nestedScrolling.revenue.text = revenue
            val runtime = "${it.runtime}m"
            nestedScrolling.runtime.text = runtime

            nestedScrolling.overview.text = it.overview

            motionLayout.floatingActionButton.setImageResource(if (it.watchLater) R.drawable.ic_playlist_add_check else R.drawable.ic_playlist_add)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loading() = binding.apply {
        shimmerLayout.startShimmer()
        shimmerLayout.isVisible = true
        nestedScrolling.nested.isVisible = false
        appBarLayout.isVisible = false
        errorLayout.isVisible = false
    }

    private fun notLoading() = binding.apply {
        shimmerLayout.stopShimmer()
        shimmerLayout.isVisible = false
        nestedScrolling.nested.isVisible = true
        appBarLayout.isVisible = true
        errorLayout.isVisible = false
    }

    private fun error() = binding.apply {
        shimmerLayout.stopShimmer()
        shimmerLayout.isVisible = false
        nestedScrolling.nested.isVisible = false
        appBarLayout.isVisible = false
        errorLayout.isVisible = true
    }

    private fun navigateUp() {
        viewModel.clearMovieId()
        //loading()
        binding.shimmerLayout.isVisible = true
        binding.nestedScrolling.nested.isVisible = false
        binding.appBarLayout.isVisible = false
        binding.errorLayout.isVisible = false
        findNavController().popBackStack()
        //findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearMovieId()
    }
}