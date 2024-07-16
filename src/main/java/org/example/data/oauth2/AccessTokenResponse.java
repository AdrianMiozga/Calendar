package org.example.data.oauth2;

import com.google.gson.annotations.SerializedName;


public record AccessTokenResponse(@SerializedName("access_token") String accessToken,
        @SerializedName("refresh_token") String refreshToken, @SerializedName("expires_in") Long expiresIn) {

}
