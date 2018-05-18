package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

public class ArtistDetails extends Artist {
    @SerializedName("bio")
    public ArtistBiography biography;
    public ArtistStats stats;
    public ArtistMatches similar;
}
