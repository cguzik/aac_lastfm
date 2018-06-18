package cg.lastfm.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.util.Log
import cg.lastfm.api.LastFMService
import cg.lastfm.api.LastFMService.Companion.API_KEY
import cg.lastfm.api.LastFMService.Companion.ARTIST_DETAILS_METHOD
import cg.lastfm.api.LastFMService.Companion.JSON_FORMAT
import cg.lastfm.data.Artist
import cg.lastfm.data.ArtistDetailsResult
import cg.lastfm.datasource.NetworkState
import cg.lastfm.datasource.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ArtistDetailsViewModel @Inject
constructor(private val lastFMService: LastFMService) : ViewModel() {

    val nameLiveData = MutableLiveData<String>()
    val networkStateLiveData = MutableLiveData<NetworkState>()
    val artistLiveData: LiveData<Artist>

    init {
        networkStateLiveData.value = NetworkState.LOADING

        artistLiveData = switchMap(nameLiveData) { this.loadArtistDetailsLiveData(it) }
    }

    private fun loadArtistDetailsLiveData(name: String): LiveData<Artist> {
        val newArtistDetailsLiveData = MutableLiveData<Artist>()

        lastFMService.getArtistDetails(ARTIST_DETAILS_METHOD, name, API_KEY, JSON_FORMAT).enqueue(object : Callback<ArtistDetailsResult> {
            override fun onResponse(call: Call<ArtistDetailsResult>, response: Response<ArtistDetailsResult>) {
                if (response.isSuccessful && response.code() == 200) {
                    newArtistDetailsLiveData.postValue(response.body()?.artist)
                    networkStateLiveData.postValue(NetworkState.LOADED)
                } else {
                    postErrorMessage(response.message())
                }
            }

            override fun onFailure(call: Call<ArtistDetailsResult>, t: Throwable) {
                postErrorMessage(t.message)
            }
        })

        return newArtistDetailsLiveData
    }

    private fun postErrorMessage(errorMessage: String?) {
        Log.e(TAG + ": API CALL", errorMessage)
        networkStateLiveData.postValue(NetworkState(Status.FAILED, errorMessage
                ?: "Unknown exception error"))
    }

    companion object {
        private val TAG = ArtistDetailsViewModel::class.java.getSimpleName()
    }
}
