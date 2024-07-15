package org.example.data;

import com.google.gson.annotations.SerializedName;


public record OAuthResponse(@SerializedName("access_token") String accessToken,
        @SerializedName("expires_in") Long expiresIn) {

}
