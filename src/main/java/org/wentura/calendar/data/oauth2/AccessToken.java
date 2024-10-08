package org.wentura.calendar.data.oauth2;

import java.io.Serializable;

public record AccessToken(String accessToken, String refreshToken, String dateTimeExpires)
        implements Serializable {}
