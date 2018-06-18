package cg.lastfm.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import cg.lastfm.ui.ArtistDetailsViewModel
import cg.lastfm.ui.ArtistsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ArtistsViewModel::class)
    abstract fun bindArtistsViewModel(artistsViewModel: ArtistsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArtistDetailsViewModel::class)
    abstract fun bindSearchViewModel(artistDetailsViewModel: ArtistDetailsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
