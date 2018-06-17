package cg.lastfm.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import cg.lastfm.ui.ArtistDetailsViewModel;
import cg.lastfm.ui.ArtistsViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ArtistsViewModel.class)
    abstract ViewModel bindArtistsViewModel(ArtistsViewModel artistsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ArtistDetailsViewModel.class)
    abstract ViewModel bindSearchViewModel(ArtistDetailsViewModel artistDetailsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
