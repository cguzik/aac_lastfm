package cg.lastfm.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import cg.lastfm.api.LastFMApi;
import cg.lastfm.api.LastFMService;
import cg.lastfm.data.ArtistDetails;
import cg.lastfm.data.ArtistDetailsResult;
import cg.lastfm.datasource.NetworkState;
import cg.lastfm.datasource.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cg.lastfm.api.LastFMService.API_KEY;
import static cg.lastfm.api.LastFMService.ARTIST_DETAILS_METHOD;
import static cg.lastfm.api.LastFMService.JSON_FORMAT;

public class ArtistDetailsViewModel extends ViewModel {
    private static final String TAG = ArtistDetailsViewModel.class.getSimpleName();

    private final MutableLiveData<String> nameLiveData = new MutableLiveData<>();
    private final MutableLiveData<NetworkState> networkStateLiveData = new MutableLiveData<>();
    private final LiveData<ArtistDetails> artistLiveData;

    public ArtistDetailsViewModel() {
        networkStateLiveData.setValue(NetworkState.LOADING);

        artistLiveData = Transformations.switchMap(nameLiveData, this::loadArtistDetailsLiveData);
    }

    @NonNull
    private LiveData<ArtistDetails> loadArtistDetailsLiveData(String name) {
        LastFMService webService = LastFMApi.createLastFMService();

        final MutableLiveData<ArtistDetails> newArtistDetailsLiveData = new MutableLiveData<>();

        webService.getArtistDetails(ARTIST_DETAILS_METHOD, name, API_KEY, JSON_FORMAT).enqueue(new Callback<ArtistDetailsResult>() {
            @Override
            public void onResponse(@NonNull Call<ArtistDetailsResult> call, @NonNull Response<ArtistDetailsResult> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    newArtistDetailsLiveData.postValue(response.body().artist);
                    networkStateLiveData.postValue(NetworkState.LOADED);
                } else {
                    postErrorMessage(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArtistDetailsResult> call, @NonNull Throwable t) {
                postErrorMessage(t.getMessage());
            }
        });

        return newArtistDetailsLiveData;
    }

    private void postErrorMessage(String errorMessage) {
        Log.e(TAG + ": API CALL", errorMessage);
        networkStateLiveData.postValue(new NetworkState(Status.FAILED, errorMessage));
    }

    public MutableLiveData<String> getNameLiveData() {
        return nameLiveData;
    }

    public MutableLiveData<NetworkState> getNetworkStateLiveData() {
        return networkStateLiveData;
    }

    public LiveData<ArtistDetails> getArtistLiveData() {
        return artistLiveData;
    }
}
