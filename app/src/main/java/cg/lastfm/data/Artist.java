package cg.lastfm.data;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;

import com.google.gson.annotations.SerializedName;

import java.util.Iterator;
import java.util.List;

public class Artist {
    public static DiffCallback<Artist> DIFF_CALLBACK = new DiffCallback<Artist>() {
        @Override
        public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
            return oldItem.equals(newItem);
        }
    };

    @SerializedName("mbid")
    public String id;

    public String name;
    public long listeners;

    public String url;

    @SerializedName("image")
    public List<ImageURL> imageURLS;

    public ImageURL getImageURL(ImageURL.Size size) {
        for (ImageURL imageURL : imageURLS) {
            if (size.equals(imageURL.size)) {
                return imageURL;
            }
        }
        throw new RuntimeException("Size " + size.name() + " not available for artist " + name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Artist artist = (Artist) obj;

        boolean same =
                artist.id == this.id
                        && artist.name == this.name
                        && artist.listeners == this.listeners
                        && artist.url == this.url
                        && artist.imageURLS.size() == this.imageURLS.size();

        if (same) {
            Iterator<ImageURL> thisImageIterator = this.imageURLS.iterator();
            Iterator<ImageURL> theOtherImageIterator = artist.imageURLS.iterator();

            while (thisImageIterator.hasNext() && same) {
                same = thisImageIterator.next().equals(theOtherImageIterator.next());
            }
        }

        return same;
    }
}
