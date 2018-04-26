package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

class ImageURL {
    @SerializedName("#text")
    public String url;

    public String size;

    @Override
    public boolean equals(Object other) {
        ImageURL otherImageURL = (ImageURL) other;
        return url == otherImageURL.url && size == otherImageURL.size;
    }

}
