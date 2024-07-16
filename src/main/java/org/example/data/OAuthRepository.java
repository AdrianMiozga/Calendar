package org.example.data;

import org.example.api.OAuthService;
import org.example.config.Constants;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class OAuthRepository {

    public static final String BASE_URL = "https://oauth2.googleapis.com/";

    private final OAuthService OAuthRepository = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OAuthService.class);

    public Response<OAuthResponse> getAccessToken(String authorizationCode) {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(Constants.PROPERTIES_FILE));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        Call<OAuthResponse> OAuthResponse =
                OAuthRepository.getAccessToken(authorizationCode, properties.getProperty(Constants.CLIENT_ID),
                        properties.getProperty(Constants.CLIENT_SECRET), properties.getProperty(Constants.REDIRECT_URI),
                        "authorization_code");

        try {
            return OAuthResponse.execute();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
