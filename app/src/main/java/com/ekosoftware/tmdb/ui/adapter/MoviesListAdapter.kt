package com.ekosoftware.tmdb.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.tmdb.app.GlideApp
import com.ekosoftware.tmdb.data.model.Movie
import com.ekosoftware.tmdb.databinding.ItemMovieBinding

class MoviesListAdapter(
    private var onSelected: (Movie) -> Unit
) :
    ListAdapter<Movie, MoviesListAdapter.MoviesListViewHolder>(MovieDiffCallback()) {

    inner class MoviesListViewHolder(
        private val binding: ItemMovieBinding,
        private val context: Context,
        private val onSelected: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie) {
            binding.root.setOnClickListener {
                onSelected(item)
            }
            val url = "https://image.tmdb.org/t/p/w500" + item.posterPath
            GlideApp.with(context).load(url).into(binding.poster)
            var title = if (item.title.isEmpty()) item.name else item.title
            if (item.releaseDate.isNotEmpty()) title += " (${item.releaseDate.split("-")[0]})"
            binding.title.text = title
            binding.rating.text = item.voteAverage.toString()
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

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}
