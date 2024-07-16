package org.example.data;

import java.io.Serializable;


public record AccessToken(String accessToken, String dateTimeExpires) implements Serializable {

}
