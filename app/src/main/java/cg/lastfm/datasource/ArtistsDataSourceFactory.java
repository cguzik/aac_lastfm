package cg.lastfm.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.DataSource.Factory;
import android.support.annotation.NonNull;

import cg.lastfm.data.Artist;

public class ArtistsDataSourceFactory extends Factory<Integer, Artist> {

    private final MutableLiveData<ArtistsDataSource> mutableLiveData;
    private final String query;

    public ArtistsDataSourceFactory(@NonNull String query) {
        this.mutableLiveData = new MutableLiveData<ArtistsDataSource>();
        this.query = query;
    }


    @Override
    public DataSource<Integer, Artist> create() {
        ArtistsDataSource artistsDataSource = new ArtistsDataSource(query);
        mutableLiveData.postValue(artistsDataSource);
        return artistsDataSource;
    }

    public MutableLiveData<ArtistsDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
