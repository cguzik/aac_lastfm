package cg.lastfm.api;


import cg.lastfm.data.ArtistSearchResults;
import cg.lastfm.data.TopArtistsSearchResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LastFMService {
    String TOP_ARTISTS_METHOD = "chart.getTopArtists";
    String SEARCH_ARTISTS_METHOD = "artist.search";

    String API_KEY = "f761ead25660962a1d09b9478a4b7cf6";
    String JSON_FORMAT = "json";

    @GET("/2.0")
    Call<ArtistSearchResults> searchArtists(
            @Query("method") String method,
            @Query("artist") String artist,
            @Query("page") int page,
            @Query("limit") int perPage,
            @Query("api_key") String api_key,
            @Query("format") String format
    );

    @GET("/2.0")
    Call<TopArtistsSearchResults> searchTopArtists(
            @Query("method") String method,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("api_key") String api_key,
            @Query("format") String format
    );

}
