package cg.lastfm.datasource;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.DataSource.Factory;
import android.support.annotation.NonNull;

import cg.lastfm.data.Artist;

public class ArtistsDataSourceFactory extends Factory<Integer, Artist> {

    private final MutableLiveData<ArtistsDataSource> artistsDataSourceLiveData;
    private final String query;

    public ArtistsDataSourceFactory(@NonNull String query) {
        this.artistsDataSourceLiveData = new MutableLiveData<ArtistsDataSource>();
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public DataSource<Integer, Artist> create() {
        ArtistsDataSource artistsDataSource = new ArtistsDataSource(query);
        artistsDataSourceLiveData.postValue(artistsDataSource);
        return artistsDataSource;
    }

    public LiveData<ArtistsDataSource> getArtistsDataSourceLiveData() {
        return artistsDataSourceLiveData;
    }
}
