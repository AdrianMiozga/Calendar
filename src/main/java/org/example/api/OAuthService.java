package org.example.api;

import org.example.data.oauth2.AccessTokenResponse;
import org.example.data.oauth2.RefreshTokenResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface OAuthService {

    @POST("token")
    Call<AccessTokenResponse> getAccessToken(@Query("code") String authorizationCode,
            @Query("client_id") String clientId, @Query("client_secret") String clientSecret,
            @Query("redirect_uri") String redirectURI, @Query("grant_type") String grantType);

    @POST("token")
    Call<RefreshTokenResponse> getRefreshToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret,
            @Query("grant_type") String grantType, @Query("refresh_token") String refreshToken);

}
