package cg.lastfm.ui

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import cg.lastfm.R
import cg.lastfm.data.Artist
import cg.lastfm.data.ImageURL.Size.MEDIUM
import cg.lastfm.datasource.NetworkState
import cg.lastfm.datasource.Status
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.CropCircleTransformation

class ArtistAdapter(private val itemClickListener: ListItemClickListener) : PagedListAdapter<Artist, RecyclerView.ViewHolder>(Artist.DIFF_CALLBACK) {
    private var networkState: NetworkState = NetworkState.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View
        val viewHolder: RecyclerView.ViewHolder

        when (viewType) {
            R.layout.artist_list_item -> {
                view = layoutInflater.inflate(R.layout.artist_list_item, parent, false)
                viewHolder = ArtistItemViewHolder(view, itemClickListener)
            }
            R.layout.network_state_item -> {
                view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
                viewHolder = NetworkStateItemViewHolder(view, itemClickListener)
            }
            else -> throw IllegalArgumentException("unknown view type")
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.artist_list_item -> (holder as ArtistItemViewHolder).bindTo(getItem(position)!!)
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindView(networkState)
        }
    }

    private fun hasExtraRow(): Boolean = networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == super.getItemCount()) {
            R.layout.network_state_item
        } else {
            R.layout.artist_list_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousItemCount = itemCount
        val previousState = networkState
        val previousExtraRow = hasExtraRow()
        networkState = newNetworkState ?: NetworkState.LOADING
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(previousItemCount)
            } else {
                notifyItemInserted(previousItemCount + 1)
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(previousItemCount - 1)
        }
    }

    internal class ArtistItemViewHolder(itemView: View, itemClickListener: ListItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val listenersNumber: TextView = itemView.findViewById(R.id.listeners)
        private val icon: ImageView = itemView.findViewById(R.id.icon)

        init {
            itemView.setOnClickListener {
                itemClickListener.onClick(it, adapterPosition)
            }
        }

        fun bindTo(artist: Artist) {
            name.text = artist.name
            listenersNumber.text = artist.listeners.toString()
            val context = icon.context
            Glide.with(context)
                    .load(artist.getImageURL(MEDIUM).url)
                    .placeholder(R.drawable.lastfm_round)
                    .error(R.drawable.lastfm_round)
                    .bitmapTransform(CropCircleTransformation(context))
                    .into(icon)
        }
    }

    internal class NetworkStateItemViewHolder(itemView: View, listItemClickListener: ListItemClickListener) : RecyclerView.ViewHolder(itemView) {

        private val progressBar: ProgressBar
        private val errorMsg: TextView
        private val button: Button

        init {
            progressBar = itemView.findViewById(R.id.progress_bar)
            errorMsg = itemView.findViewById(R.id.error_msg)
            button = itemView.findViewById(R.id.retry_button)

            button.setOnClickListener { view -> listItemClickListener.onClick(view, adapterPosition) }
        }


        fun bindView(networkState: NetworkState?) {
            if (networkState != null && networkState.status === Status.RUNNING) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }

            if (networkState != null && networkState.status === Status.FAILED) {
                errorMsg.visibility = View.VISIBLE
                errorMsg.text = networkState.msg
                button.visibility = View.VISIBLE
            } else {
                errorMsg.visibility = View.GONE
                button.visibility = View.GONE
            }
        }
    }
}
