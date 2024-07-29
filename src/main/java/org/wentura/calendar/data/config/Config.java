package org.wentura.calendar.data.config;

import java.net.MalformedURLException;
import java.net.URL;

public record Config(String clientId, String clientSecret, String redirectURI) {

    private static URL url;

    public Config {
        try {
            url = new URL(redirectURI);
        } catch (MalformedURLException e) {
            System.out.println("Invalid redirect URI");
            System.exit(1);
        }
    }

    public int getPort() {
        return url.getPort();
    }

    public String getRedirectPath() {
        return url.getPath();
    }
}
