package org.example.api;

import org.example.data.OAuthResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface OAuthService {

    @POST("token")
    Call<OAuthResponse> getResponse(@Query("code") String code, @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret, @Query("redirect_uri") String redirectURI,
            @Query("grant_type") String grantType);

}
