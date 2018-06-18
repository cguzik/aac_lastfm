package cg.lastfm.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import cg.lastfm.api.LastFMService
import cg.lastfm.data.Artist
import cg.lastfm.datasource.ArtistsDataSource
import cg.lastfm.datasource.ArtistsDataSourceFactory
import cg.lastfm.datasource.NetworkState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class ArtistsViewModel @Inject
constructor(private val webService: LastFMService) : ViewModel() {
    private val executor: ExecutorService = Executors.newFixedThreadPool(5)
    val artistsList: LiveData<PagedList<Artist>>
    val queryLiveData: MutableLiveData<String> = MutableLiveData()
    private val artistsDataSourceFactoryLiveData: LiveData<ArtistsDataSourceFactory>
    private val artistsDataSourceLiveData: LiveData<ArtistsDataSource>
    val networkState: LiveData<NetworkState>

    init {
        queryLiveData.value = ""

        artistsDataSourceFactoryLiveData = switchMap(queryLiveData) {
            val artistsDataSourceFactoryLiveData = MutableLiveData<ArtistsDataSourceFactory>()
            artistsDataSourceFactoryLiveData.setValue(ArtistsDataSourceFactory(it, webService))
            artistsDataSourceFactoryLiveData
        }

        artistsDataSourceLiveData = switchMap(artistsDataSourceFactoryLiveData) { it.artistsDataSourceLiveData }

        networkState = switchMap(artistsDataSourceLiveData) { it.networkState }

        artistsList = switchMap(artistsDataSourceFactoryLiveData
        ) { dataSourceFactory ->
            val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(PAGE_SIZE)
                    .setPageSize(PAGE_SIZE)
                    .build()

            LivePagedListBuilder(dataSourceFactory, pagedListConfig)
                    .setFetchExecutor(executor)
                    .build()
        }
    }

    fun refreshLoadedData() {
        val currentQuery = queryLiveData.value
        queryLiveData.value = currentQuery
    }

    companion object {
        private val PAGE_SIZE = 16
    }
}
