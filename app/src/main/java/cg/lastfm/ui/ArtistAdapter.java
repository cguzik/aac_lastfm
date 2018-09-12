package cg.lastfm.ui;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cg.lastfm.R;
import cg.lastfm.data.Artist;
import cg.lastfm.datasource.NetworkState;
import cg.lastfm.datasource.Status;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static cg.lastfm.data.ImageURL.Size.MEDIUM;

public class ArtistAdapter extends PagedListAdapter<Artist, RecyclerView.ViewHolder> {

    private static final String TAG = "UserAdapter";
    private final ListItemClickListener itemClickListener;
    private NetworkState networkState;

    public ArtistAdapter(ListItemClickListener itemClickListener) {
        super(Artist.DIFF_CALLBACK);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case R.layout.artist_list_item:
                view = layoutInflater.inflate(R.layout.artist_list_item, parent, false);
                viewHolder = new ArtistItemViewHolder(view, itemClickListener);
                break;
            case R.layout.network_state_item:
                view = layoutInflater.inflate(R.layout.network_state_item, parent, false);
                viewHolder = new NetworkStateItemViewHolder(view, itemClickListener);
                break;
            default:
                throw new IllegalArgumentException("unknown view type");
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.artist_list_item:
                ((ArtistItemViewHolder) holder).bindTo(getItem(position));
                break;
            case R.layout.network_state_item:
                ((NetworkStateItemViewHolder) holder).bindView(networkState);
                break;
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.artist_list_item;
        }
    }

    @Override
    public int getItemCount() {
        int extraRowsNumber = hasExtraRow() ? 1 : 0;
        return super.getItemCount() + extraRowsNumber;
    }

    public void setNetworkState(NetworkState newNetworkState) {
        int itemCount = getItemCount();
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount);
            } else {
                notifyItemInserted(itemCount + 1);
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1);
        }
    }

    static class ArtistItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, listenersNumber;
        ImageView icon;

        ArtistItemViewHolder(View itemView, final ListItemClickListener itemClickListener) {
            super(itemView);
            itemView.setOnClickListener(v -> itemClickListener.onClick(v, getAdapterPosition()));
            name = itemView.findViewById(R.id.name);
            listenersNumber = itemView.findViewById(R.id.listeners);
            icon = itemView.findViewById((R.id.icon));
        }

        void bindTo(Artist artist) {
            name.setText(artist.name);
            listenersNumber.setText(String.valueOf(artist.listeners));
            Context context = icon.getContext();
            Glide.with(context)
                    .load(artist.getImageURL(MEDIUM).url)
                    .placeholder(R.drawable.lastfm_round)
                    .error(R.drawable.lastfm_round)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(icon);
        }
    }

    static class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private final ProgressBar progressBar;
        private final TextView errorMsg;
        private Button button;

        NetworkStateItemViewHolder(View itemView, ListItemClickListener listItemClickListener) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            errorMsg = itemView.findViewById(R.id.error_msg);
            button = itemView.findViewById(R.id.retry_button);

            button.setOnClickListener(view -> listItemClickListener.onClick(view, getAdapterPosition()));
        }


        void bindView(NetworkState networkState) {
            if (networkState == null || networkState.getStatus() == Status.RUNNING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == Status.FAILED) {
                errorMsg.setVisibility(View.VISIBLE);
                errorMsg.setText(networkState.getMsg());
                button.setVisibility(View.VISIBLE);
            } else {
                errorMsg.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
            }
        }
    }
}