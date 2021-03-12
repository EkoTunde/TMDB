package com.ekosoftware.tmdb.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ekosoftware.tmdb.R
import com.ekosoftware.tmdb.app.GlideApp
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.databinding.ItemMovieBinding
import com.ekosoftware.tmdb.util.asUrl

class MoviesPagerAdapter(private val onClick: (movie: Movie) -> Unit) :
    PagingDataAdapter<Movie, MoviesPagerAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

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

        init {
            binding.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { position ->
                    getItem(position)?.let { item ->
                        onClick.invoke(item)
                    }
                }
            }
        }

        fun bind(movie: Movie) {
            binding.apply {
                GlideApp.with(itemView)
                    .load(movie.posterPath.asUrl())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(poster)

                title.text = if (movie.title.isEmpty()) movie.name else movie.title
                rating.text = movie.voteAverage.toString()
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