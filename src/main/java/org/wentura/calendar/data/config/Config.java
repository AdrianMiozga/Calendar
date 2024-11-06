package org.wentura.calendar.data.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public record Config(String clientId, String clientSecret, String redirectURI) {

    private static URL url;

    public Config {
        try {
            url = new URI(redirectURI).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            System.out.println("Invalid redirect URI");
            System.exit(1);
        }
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        return url.getPort();
    }

    public String getRedirectPath() {
        return url.getPath();
    }
}
