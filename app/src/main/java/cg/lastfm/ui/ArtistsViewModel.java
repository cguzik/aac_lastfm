package cg.lastfm.ui;

import android.arch.lifecycle.LifecycleOwner;
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
    private static final int PAGE_SIZE = 11;
    private final ExecutorService executor;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<Artist>> artistsList;
    private String query = "";

    public ArtistsViewModel() {
        executor = Executors.newFixedThreadPool(5);
        initDataSource(query);
    }

    /**
     * @param query          new artists search query
     * @param lifecycleOwner associated with observers registered for this model
     * @return true if datasource has changed
     */
    public boolean notifyQueryHasChanged(String query, LifecycleOwner lifecycleOwner) {
        boolean queryHasChanged = !query.equals(this.query);
        if (queryHasChanged) {
            this.query = query;
            restartLoadingData(lifecycleOwner);
        }
        return queryHasChanged;
    }

    private void initDataSource(String query) {
        ArtistsDataSourceFactory dataSourceFactory = new ArtistsDataSourceFactory(query);

        networkState = Transformations.switchMap(dataSourceFactory.getMutableLiveData(), ArtistsDataSource::getNetworkState);

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(PAGE_SIZE)
                        .setPageSize(PAGE_SIZE)
                        .build();

        artistsList = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Artist>> getArtistsList() {
        return artistsList;
    }

    public void restartLoadingData(LifecycleOwner lifecycleOwner) {
        networkState.removeObservers(lifecycleOwner);
        artistsList.removeObservers(lifecycleOwner);
        initDataSource(query);
    }
}
