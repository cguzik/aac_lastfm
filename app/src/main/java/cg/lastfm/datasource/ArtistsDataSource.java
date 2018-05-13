package cg.lastfm.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cg.lastfm.api.LastFMApi;
import cg.lastfm.api.LastFMService;
import cg.lastfm.data.Artist;
import cg.lastfm.data.ArtistSearchResults;
import cg.lastfm.data.TopArtistsSearchResults;
import cg.lastfm.util.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistsDataSource extends PageKeyedDataSource<Integer, Artist> {

    private static final String TAG = ArtistsDataSource.class.getSimpleName();

    private final LastFMService lastFMService;
    private final MutableLiveData<NetworkState> networkState;
    private final String query;


    ArtistsDataSource(@NonNull String query) {
        lastFMService = LastFMApi.createLastFMService();
        networkState = new MutableLiveData<>();
        this.query = query;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Artist> callback) {
        loadArtists(1, params.requestedLoadSize,
                artists -> callback.onResult(artists, null, 2)
        );
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Artist> callback) {
        //Implementation not needed. There is no data before initial page loaded.
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Artist> callback) {
        Integer page = params.key;
        Integer nextPage = page + 1;
        loadArtists(page, params.requestedLoadSize,
                artists -> callback.onResult(artists, nextPage)
        );
    }

    private void loadArtists(int page, int pageSize, @NonNull Consumer<List<Artist>> consumer) {
        Log.d(TAG, "Loading Page " + page + " page size " + pageSize);
        networkState.postValue(NetworkState.LOADING);

        if (query.isEmpty()) {
            searchTopArtists(page, pageSize, consumer);
        } else {
            searchArtistsByName(query, page, pageSize, consumer);
        }

    }

    private void searchArtistsByName(String query, int page, int pageSize, Consumer<List<Artist>> consumer) {
        lastFMService.searchArtists(
                LastFMService.SEARCH_ARTISTS_METHOD,
                query,
                page,
                pageSize,
                LastFMService.API_KEY,
                LastFMService.JSON_FORMAT).enqueue(new Callback<ArtistSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<ArtistSearchResults> call, @NonNull Response<ArtistSearchResults> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    ArrayList<Artist> artists = new ArrayList<>();
                    artists.addAll(response.body().results.artistMatches.artists);
                    postLoadedData(artists, consumer);
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

    private void searchTopArtists(int page, int pageSize, @NonNull Consumer<List<Artist>> consumer) {
        lastFMService.searchTopArtists(
                LastFMService.TOP_ARTISTS_METHOD,
                page,
                pageSize,
                LastFMService.API_KEY,
                LastFMService.JSON_FORMAT).enqueue(new Callback<TopArtistsSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<TopArtistsSearchResults> call, @NonNull Response<TopArtistsSearchResults> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    ArrayList<Artist> artists = new ArrayList<>();
                    artists.addAll(response.body().artists.artists);
                    postLoadedData(artists, consumer);
                } else {
                    postNetworkError(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TopArtistsSearchResults> call, @NonNull Throwable t) {
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
