package cg.lastfm.data;

import com.google.gson.annotations.SerializedName;

public class ArtistMatchesResults {
    @SerializedName("opensearch:Query")
    public OpenSearchQuery query;

    @SerializedName("opensearch:totalResults")
    public long totalResults;

    @SerializedName("opensearch:startIndex")
    public long startIndex;

    @SerializedName("opensearch:itemsPerPage")
    public long itemsPerPage;

    @SerializedName("artistmatches")
    public ArtistMatches artistMatches;
}
