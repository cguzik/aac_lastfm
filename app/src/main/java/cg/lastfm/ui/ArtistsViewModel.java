package cg.lastfm.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cg.lastfm.data.Artist;
import cg.lastfm.datasource.ArtistsDataSource;
import cg.lastfm.datasource.ArtistsDataSourceFactory;
import cg.lastfm.datasource.NetworkState;

public class ArtistsViewModel extends ViewModel {
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<Artist>> artistsList;

    public ArtistsViewModel() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        ArtistsDataSourceFactory dataSourceFactory = new ArtistsDataSourceFactory("");

        networkState = Transformations.switchMap(dataSourceFactory.getMutableLiveData(), ArtistsDataSource::getNetworkState);

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(30)
                        .setPageSize(30)
                        .build();

        artistsList = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig)
                .setBackgroundThreadExecutor(executor)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Artist>> getArtistsList() {
        return artistsList;
    }
}
