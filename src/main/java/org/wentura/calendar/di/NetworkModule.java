package org.wentura.calendar.di;

import dagger.Module;
import dagger.Provides;

import org.wentura.calendar.api.EventService;
import org.wentura.calendar.api.OAuthService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public interface NetworkModule {

    @Provides
    static OAuthService provideOAuthService() {
        var baseUrl = "https://oauth2.googleapis.com/";

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OAuthService.class);
    }

    @Provides
    static EventService provideEventService() {
        var baseUrl = "https://www.googleapis.com/calendar/v3/";

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EventService.class);
    }
}
