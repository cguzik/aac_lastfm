package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artist {
    @SerializedName("mbid")
    public String id;

    public String name;
    public long listeners;

    public String url;

    public List<ImageURL> imageURLS;
}
