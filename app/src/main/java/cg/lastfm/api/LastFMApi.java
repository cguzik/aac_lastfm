package cg.lastfm.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LastFMApi {

    public static LastFMService createLastFMService() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://ws.audioscrobbler.com/2.0");

        return builder.build().create(LastFMService.class);
    }
}
