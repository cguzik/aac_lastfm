package cg.lastfm.data

import com.google.gson.annotations.SerializedName

data class ArtistMatches(
        @SerializedName("artist")
        val artists: List<Artist>?
)
