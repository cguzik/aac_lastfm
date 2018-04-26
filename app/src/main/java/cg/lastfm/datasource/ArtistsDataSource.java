package cg.lastfm.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cg.lastfm.api.LastFMApi;
import cg.lastfm.api.LastFMService;
import cg.lastfm.data.Artist;
import cg.lastfm.data.ArtistSearchResults;
import cg.lastfm.data.TopArtists;
import cg.lastfm.util.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ArtistsDataSource extends PositionalDataSource<Artist> {

    public static final String TAG = ArtistsDataSource.class.getSimpleName();

    private final LastFMService lastFMService;
    private final MutableLiveData<NetworkState> networkState;
    private final String query;


    public ArtistsDataSource(@NonNull String query) {
        lastFMService = LastFMApi.createLastFMService();
        networkState = new MutableLiveData<>();
        this.query = query;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Artist> callback) {
        loadArtists(params.requestedStartPosition, params.requestedLoadSize,
                artists -> callback.onResult(artists, params.requestedStartPosition)
        );
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Artist> callback) {
        loadArtists(params.startPosition, params.loadSize,
                callback::onResult
        );
    }


    private void loadArtists(int startPosition, int pageSize, @NonNull Consumer<List<Artist>> consumer) {
        Log.d(TAG, "Loading Page " + startPosition + " page size " + pageSize);
        networkState.postValue(NetworkState.LOADING);

        if (query.isEmpty()) {
            searchTopArtists(startPosition, pageSize, consumer);
        } else {
            searchArtistsByName(query, startPosition, pageSize, consumer);
        }

    }

    private void searchArtistsByName(String query, int startPosition, int pageSize, Consumer<List<Artist>> consumer) {
        lastFMService.searchArtists(
                query,
                LastFMService.SEARCH_ARTISTS_METHOD,
                startPosition,
                pageSize,
                LastFMService.API_KEY,
                LastFMService.JSON_FORMAT).enqueue(new Callback<ArtistSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<ArtistSearchResults> call, @NonNull Response<ArtistSearchResults> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    postLoadedData(response.body().artistMatches.artists, consumer);
                } else {
                    postNetworkError(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArtistSearchResults> call, @NonNull Throwable t) {
                postNetworkError(t.getMessage());
            }
        });
    }

    private void searchTopArtists(int startPosition, int pageSize, @NonNull Consumer<List<Artist>> consumer) {
        lastFMService.searchTopArtists(
                LastFMService.TOP_ARTISTS_METHOD,
                startPosition,
                pageSize,
                LastFMService.API_KEY,
                LastFMService.JSON_FORMAT).enqueue(new Callback<TopArtists>() {
            @Override
            public void onResponse(@NonNull Call<TopArtists> call, @NonNull Response<TopArtists> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    postLoadedData(response.body().artists, consumer);
                } else {
                    postNetworkError(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TopArtists> call, @NonNull Throwable t) {
                postNetworkError(t.getMessage());
            }
        });
    }

    private void postLoadedData(@NonNull List<Artist> artists, @NonNull Consumer<List<Artist>> consumer) {
        consumer.accept(artists);
        networkState.postValue(NetworkState.LOADED);
    }

    private void postNetworkError(@NonNull String errorMessage) {
        Log.e(TAG + ": API CALL", errorMessage);
        networkState.postValue(new NetworkState(Status.FAILED, errorMessage));
    }

}
