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

    public Response<OAuthResponse> getResponse(String code) {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(Constants.APPLICATION_PROPERTIES));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Call<OAuthResponse> OAuthResponse = OAuthRepository.getResponse(code, properties.getProperty("clientId"),
                properties.getProperty("clientSecret"), properties.getProperty("redirectURI"), "authorization_code");

        try {
            return OAuthResponse.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
