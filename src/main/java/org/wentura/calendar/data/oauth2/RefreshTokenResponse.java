package org.wentura.calendar.data.oauth2;

import com.google.gson.annotations.SerializedName;

public record RefreshTokenResponse(
        @SerializedName("access_token") String accessToken,
        @SerializedName("expires_in") Long expiresIn) {}
