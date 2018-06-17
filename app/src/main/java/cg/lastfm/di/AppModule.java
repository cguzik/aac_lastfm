package cg.lastfm.di;

import javax.inject.Singleton;

import cg.lastfm.ArtistDetailsActivity;
import cg.lastfm.MainActivity;
import cg.lastfm.api.LastFMService;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton
    @Provides
    LastFMService provideLastFMService() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://ws.audioscrobbler.com")
                .build()
                .create(LastFMService.class);
    }
}
