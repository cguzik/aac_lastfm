package cg.lastfm.di;

import cg.lastfm.ArtistDetailsActivity;
import cg.lastfm.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ArtistDetailsActivityModule {
    @ContributesAndroidInjector
    abstract ArtistDetailsActivity contributeArtistDetailsActivity();
}
