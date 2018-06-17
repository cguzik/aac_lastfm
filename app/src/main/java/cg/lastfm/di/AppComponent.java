package cg.lastfm.di;

import android.app.Application;

import javax.inject.Singleton;

import cg.lastfm.ArtistDetailsActivity;
import cg.lastfm.LastFMApp;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, MainActivityModule.class, ArtistDetailsActivityModule.class})
public interface AppComponent extends AndroidInjector<LastFMApp> {
}
