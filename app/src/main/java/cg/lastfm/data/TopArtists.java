package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopArtists {
    @SerializedName("artist")
    public List<Artist> artists;
}
