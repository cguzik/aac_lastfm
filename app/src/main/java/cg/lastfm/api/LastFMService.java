package cg.lastfm.api;


import java.util.List;

import cg.lastfm.data.Artist;
import cg.lastfm.data.ArtistSearchResults;
import cg.lastfm.data.TopArtists;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryName;

public interface LastFMService {
    @GET
    Call<ArtistSearchResults> searchArtists(
            @Query("method") String method,
            @Query("artist") String artist,
            @Query("page") long page,
            @Query("limit") int perPage,
            @Query("api_key") String api_key,
            @Query("format") String format
    );

    @GET
    Call<TopArtists> searchTopArtists(
            @Query("method") String method,
            @Query("page") String page,
            @Query("limit") String limit,
            @Query("api_key") String api_key,
            @Query("format") String format
    );

}
