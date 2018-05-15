package cg.lastfm.ui;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
    private static final int PAGE_SIZE = 11;
    private final ExecutorService executor;
    private final LiveData<PagedList<Artist>> artistsList;
    private final MutableLiveData<String> queryLiveData;
    private final LiveData<ArtistsDataSourceFactory> artistsDataSourceFactoryLiveData;
    private final LiveData<ArtistsDataSource> artistsDataSourceLiveData;
    private LiveData<NetworkState> networkState;

    public ArtistsViewModel() {
        executor = Executors.newFixedThreadPool(5);
        queryLiveData = new MutableLiveData<>();
        queryLiveData.setValue("");

        artistsDataSourceFactoryLiveData = Transformations.switchMap(queryLiveData, query -> {
                    MutableLiveData<ArtistsDataSourceFactory> artistsDataSourceFactoryLiveData = new MutableLiveData<>();
                    artistsDataSourceFactoryLiveData.setValue(new ArtistsDataSourceFactory(query));
                    return artistsDataSourceFactoryLiveData;
                }
        );

        artistsDataSourceLiveData = Transformations.switchMap(artistsDataSourceFactoryLiveData, ArtistsDataSourceFactory::getArtistsDataSourceLiveData);

        networkState = Transformations.switchMap(artistsDataSourceLiveData, ArtistsDataSource::getNetworkState);

        artistsList = Transformations.switchMap(artistsDataSourceFactoryLiveData, dataSourceFactory -> {
                    PagedList.Config pagedListConfig =
                            (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                                    .setInitialLoadSizeHint(PAGE_SIZE)
                                    .setPageSize(PAGE_SIZE)
                                    .build();

                    return new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig)
                            .setFetchExecutor(executor)
                            .build();
                }
        );
    }

    public MutableLiveData<String> getQueryLiveData() {
        return queryLiveData;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Artist>> getArtistsList() {
        return artistsList;
    }

    public void refreshLoadedData() {
        String currentQuery = queryLiveData.getValue();
        queryLiveData.setValue(currentQuery);
    }
}
