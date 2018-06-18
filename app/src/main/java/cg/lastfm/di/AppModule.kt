package cg.lastfm.di

import cg.lastfm.api.LastFMService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
class AppModule {
    @Singleton
    @Provides
    fun provideLastFMService(): LastFMService {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://ws.audioscrobbler.com")
                .build()
                .create(LastFMService::class.java)
    }
}
