package cg.lastfm.datasource

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.DataSource.Factory

import cg.lastfm.api.LastFMService
import cg.lastfm.data.Artist

class ArtistsDataSourceFactory(val query: String, private val webService: LastFMService) : Factory<Int, Artist>() {

    private val mutableLiveData: MutableLiveData<ArtistsDataSource> = MutableLiveData()
    val artistsDataSourceLiveData: LiveData<ArtistsDataSource>
        get() = mutableLiveData

    override fun create(): DataSource<Int, Artist> {
        val artistsDataSource = ArtistsDataSource(query, webService)
        mutableLiveData.postValue(artistsDataSource)
        return artistsDataSource
    }
}
