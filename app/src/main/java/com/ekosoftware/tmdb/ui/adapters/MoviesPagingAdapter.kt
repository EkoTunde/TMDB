package com.ekosoftware.tmdb.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.GlideApp
import com.ekosoftware.tmdb.app.Strings
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.databinding.ItemMovieBinding
import com.ekosoftware.tmdb.util.asUrl
import com.google.android.material.card.MaterialCardView

class MoviesPagingAdapter(private val onClick: (movie: Movie, cardView: MaterialCardView) -> Unit) :
    PagingDataAdapter<Movie, MoviesPagingAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            holder.bind(movie)
        }
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                GlideApp.with(itemView)
                    .load(movie.posterPath.asUrl())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(poster)

                container.transitionName =
                    Strings.get(R.string.movie_card_transition_name, movie.id.toString())

                val titleText = "${if (movie.title.isEmpty()) movie.name else movie.title} (${movie.releaseDate.split("-")[0]})"

                title.text = titleText
                rating.text = movie.voteAverage.toString()

                container.setOnClickListener {
                    onClick.invoke(movie, this.container)
                }
            }
        }
    }

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }
        private const val TAG = "MoviesAdapter"
    }
}