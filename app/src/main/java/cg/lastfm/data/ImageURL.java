package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

public class ImageURL {
    @SerializedName("#text")
    public String url;

    public Size size;

    @Override
    public boolean equals(Object other) {
        ImageURL otherImageURL = (ImageURL) other;
        return url == otherImageURL.url && size == otherImageURL.size;
    }

    public enum Size {
        @SerializedName("small") SMALL,
        @SerializedName("medium") MEDIUM,
        @SerializedName("large") LARGE,
        @SerializedName("extralarge") EXTRALARGE,
        @SerializedName("mega") MEGA;
    }
}
