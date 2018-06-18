package cg.lastfm.data

import com.google.gson.annotations.SerializedName

data class ArtistMatchesResults(
        @SerializedName("opensearch:Query")
        val query: OpenSearchQuery?,

        @SerializedName("opensearch:totalResults")
        val totalResults: Long,

        @SerializedName("opensearch:startIndex")
        val startIndex: Long,

        @SerializedName("opensearch:itemsPerPage")
        val itemsPerPage: Long,

        @SerializedName("artistmatches")
        val artistMatches: ArtistMatches?
)
