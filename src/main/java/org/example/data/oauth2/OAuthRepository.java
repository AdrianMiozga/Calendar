package org.example.data.oauth2;

import org.example.api.OAuthService;
import org.example.config.Constants;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Properties;

import static org.example.util.Util.getAppProperties;


public class OAuthRepository {

    public static final String BASE_URL = "https://oauth2.googleapis.com/";

    private final OAuthService OAuthRepository = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OAuthService.class);

    public Response<AccessTokenResponse> getAccessToken(String authorizationCode) {
        Properties properties = getAppProperties();

        Call<AccessTokenResponse> call =
                OAuthRepository.getAccessToken(authorizationCode, properties.getProperty(Constants.CLIENT_ID),
                        properties.getProperty(Constants.CLIENT_SECRET), properties.getProperty(Constants.REDIRECT_URI),
                        "authorization_code");

        try {
            return call.execute();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Response<RefreshTokenResponse> getRefreshToken(String refreshToken) {
        Properties properties = getAppProperties();

        Call<RefreshTokenResponse> call = OAuthRepository.getRefreshToken(properties.getProperty(Constants.CLIENT_ID),
                properties.getProperty(Constants.CLIENT_SECRET), "refresh_token", refreshToken);

        try {
            return call.execute();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
