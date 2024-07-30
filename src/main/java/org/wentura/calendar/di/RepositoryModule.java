package org.wentura.calendar.di;

import dagger.Module;
import dagger.Provides;

import org.wentura.calendar.api.OAuthService;
import org.wentura.calendar.data.config.ConfigRepository;
import org.wentura.calendar.data.oauth2.OAuthRepository;

import javax.inject.Singleton;

@Module
public interface RepositoryModule {

    @Provides
    @Singleton
    static ConfigRepository provideConfigRepository() {
        return new ConfigRepository();
    }

    @Provides
    @Singleton
    static OAuthRepository provideOAuthRepository(
            OAuthService OAuthService, ConfigRepository configRepository) {
        return new OAuthRepository(OAuthService, configRepository);
    }
}
