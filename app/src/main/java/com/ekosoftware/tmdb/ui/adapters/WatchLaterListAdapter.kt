package com.ekosoftware.tmdb.ui.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.tmdb.app.GlideApp
import com.ekosoftware.tmdb.data.model.MovieEntity
import com.ekosoftware.tmdb.databinding.ItemMovieBinding
import com.google.android.material.card.MaterialCardView

class WatchLaterListAdapter(
    private var onSelected: (movieEntity: MovieEntity, materialCardView: MaterialCardView) -> Unit
) :
    ListAdapter<MovieEntity, WatchLaterListAdapter.MovieEntitiesListViewHolder>(
        MovieEntityDiffCallback()
    ) {

    inner class MovieEntitiesListViewHolder(
        private val binding: ItemMovieBinding,
        private val onSelected: (movieEntity: MovieEntity, materialCardView: MaterialCardView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieEntity) {
            binding.root.setOnClickListener {
                onSelected(item, binding.container)
            }
            binding.root.transitionName = item.id.toString()
            val url = "https://image.tmdb.org/t/p/w500" + item.posterPath
            GlideApp.with(binding.root).load(url).into(binding.poster)
            var title = if (item.title.isEmpty()) item.name else item.title
            if (item.releaseDate.isNotEmpty()) title += " (${item.releaseDate.split("-")[0]})"
            binding.title.text = title
            binding.rating.text = item.rating.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieEntitiesListViewHolder {
        return MovieEntitiesListViewHolder(
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onSelected
        )
    }


    private val TAG = "WatchLaterListAdapter"
    override fun onBindViewHolder(holderList: MovieEntitiesListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${getItem(position)}")
        holderList.bind(getItem(position))
    }
}

class MovieEntityDiffCallback : DiffUtil.ItemCallback<MovieEntity>() {
    override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean =
        oldItem == newItem
}
