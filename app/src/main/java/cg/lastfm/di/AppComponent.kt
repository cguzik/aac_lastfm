package cg.lastfm.di

import cg.lastfm.LastFMApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, AppModule::class, MainActivityModule::class, ArtistDetailsActivityModule::class))
interface AppComponent : AndroidInjector<LastFMApp>
