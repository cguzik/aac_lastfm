package cg.lastfm.di

import cg.lastfm.ArtistDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ArtistDetailsActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeArtistDetailsActivity(): ArtistDetailsActivity
}
