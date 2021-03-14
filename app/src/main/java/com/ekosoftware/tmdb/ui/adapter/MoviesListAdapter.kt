package com.ekosoftware.tmdb.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.tmdb.app.GlideApp
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.databinding.ItemMovieBinding
import com.google.android.material.card.MaterialCardView

class MoviesListAdapter(
    private var onSelected: (movie: MovieEntity, cardView: MaterialCardView) -> Unit
) :
    ListAdapter<MovieEntity, MoviesListAdapter.MoviesListViewHolder>(MovieDiffCallback()) {

    inner class MoviesListViewHolder(
        private val binding: ItemMovieBinding,
        private val context: Context,
        private val onSelected: (movie: MovieEntity, cardView: MaterialCardView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieEntity) {
            binding.root.setOnClickListener {
                onSelected(item, binding.root)
            }
            binding.root.transitionName = item.id.toString()
            val url = "https://image.tmdb.org/t/p/w500" + item.posterPath
            GlideApp.with(context).load(url).into(binding.poster)
            var title = if (item.title.isEmpty()) item.name else item.title
            if (item.releaseDate.isNotEmpty()) title += " (${item.releaseDate.split("-")[0]})"
            binding.title.text = title
            binding.rating.text = item.rating.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesListViewHolder {
        return MoviesListViewHolder(
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context,
            onSelected
        )
    }

    override fun onBindViewHolder(holderList: MoviesListViewHolder, position: Int) {
        holderList.bind(getItem(position))
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
    override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean = oldItem == newItem
}
