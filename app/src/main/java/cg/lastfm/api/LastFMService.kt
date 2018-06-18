package cg.lastfm.api


import cg.lastfm.data.ArtistDetailsResult
import cg.lastfm.data.ArtistSearchResults
import cg.lastfm.data.TopArtistsSearchResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFMService {

    @GET("/2.0")
    fun searchArtists(
            @Query("method") method: String,
            @Query("artist") artist: String,
            @Query("page") page: Int,
            @Query("limit") perPage: Int,
            @Query("api_key") api_key: String,
            @Query("format") format: String
    ): Call<ArtistSearchResults>

    @GET("/2.0")
    fun searchTopArtists(
            @Query("method") method: String,
            @Query("page") page: Int,
            @Query("limit") limit: Int,
            @Query("api_key") api_key: String,
            @Query("format") format: String
    ): Call<TopArtistsSearchResults>

    @GET("/2.0")
    fun getArtistDetails(
            @Query("method") method: String,
            @Query("artist") artist: String,
            @Query("api_key") api_key: String,
            @Query("format") format: String
    ): Call<ArtistDetailsResult>

    companion object {
        val TOP_ARTISTS_METHOD = "chart.getTopArtists"
        val SEARCH_ARTISTS_METHOD = "artist.search"
        val ARTIST_DETAILS_METHOD = "artist.getinfo"

        val API_KEY = "f761ead25660962a1d09b9478a4b7cf6"
        val JSON_FORMAT = "json"
    }

}
