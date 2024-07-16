package org.example.data.oauth2;

import com.sun.net.httpserver.HttpServer;
import org.example.api.OAuthService;
import org.example.config.Constants;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

import static org.example.util.Util.getAppProperties;
import static org.example.util.Util.queryToMap;


public class OAuthRepository {

    public static final String BASE_URL = "https://oauth2.googleapis.com/";

    private final OAuthService OAuthService = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OAuthService.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    private HttpServer httpServer;
    private String authorizationCode;

    private static boolean isTokenExpired(AccessToken accessToken) {
        return LocalDateTime.parse(accessToken.dateTimeExpires()).isBefore(LocalDateTime.now());
    }

    private static AccessToken readSerializedToken() {
        AccessToken accessToken = null;

        if (Files.exists(Paths.get(Constants.ACCESS_TOKEN_FILE))) {
            try (FileInputStream fileIn = new FileInputStream(Constants.ACCESS_TOKEN_FILE);
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {
                accessToken = (AccessToken) in.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        return accessToken;
    }

    private static void writeSerializedToken(AccessToken accessToken) {
        try (FileOutputStream fileOut = new FileOutputStream(Constants.ACCESS_TOKEN_FILE);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(accessToken);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getAccessToken() {
        AccessToken accessToken = readSerializedToken();

        if (accessToken == null) {
            var authorizationCode = getAuthorizationCode();

            var properties = getAppProperties();

            Call<AccessTokenResponse> call =
                    OAuthService.getAccessToken(authorizationCode, properties.getProperty(Constants.CLIENT_ID),
                            properties.getProperty(Constants.CLIENT_SECRET),
                            properties.getProperty(Constants.REDIRECT_URI), "authorization_code");

            AccessTokenResponse accessTokenResponse;

            try {
                accessTokenResponse = call.execute().body();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            if (accessTokenResponse == null) {
                throw new IllegalStateException("Access token is null");
            }

            var newAccessToken = new AccessToken(accessTokenResponse.accessToken(), accessTokenResponse.refreshToken(),
                    LocalDateTime.now().plusSeconds(accessTokenResponse.expiresIn()).toString());

            writeSerializedToken(newAccessToken);

            return newAccessToken.accessToken();
        } else if (isTokenExpired(accessToken)) {
            return getNewAccessToken();
        } else {
            return accessToken.accessToken();
        }
    }

    private String getAuthorizationCode() {
        var properties = getAppProperties();

        var scopes = new String[] { "https://www.googleapis.com/auth/calendar.readonly",
                "https://www.googleapis.com/auth/calendar.events.readonly" };

        var uri =
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + properties.getProperty(Constants.CLIENT_ID)
                        + "&redirect_uri=" + properties.getProperty(Constants.REDIRECT_URI)
                        + "&response_type=code&scope=" + String.join("+", scopes) + "&access_type=offline";

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(uri));
            } catch (URISyntaxException | IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        try {
            httpServer =
                    HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty(Constants.PORT))),
                            0);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        httpServer.createContext(properties.getProperty(Constants.REDIRECT_PATH), httpExchange -> {
            var code = queryToMap(httpExchange.getRequestURI().getQuery()).get("code");

            if (code == null) {
                throw new IllegalStateException("Code parameter not found");
            }

            authorizationCode = code;

            var response = """
                    <div style="margin: 0; height: 100vh; display: flex; justify-content: center; align-items: center;">
                        <div style="text-align: center; font-size: 32px;">
                            <p>You can close this tab now</p>
                        </div>
                    </div>""";

            httpExchange.sendResponseHeaders(200, response.length());

            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();

            httpServer.stop(0);

            latch.countDown();
        });

        httpServer.setExecutor(null);
        httpServer.start();

        try {
            latch.await();
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }

        return authorizationCode;
    }

    private String getNewAccessToken() {
        AccessToken accessToken = readSerializedToken();

        if (accessToken == null) {
            throw new IllegalStateException("Access token is null");
        }

        var properties = getAppProperties();

        Call<RefreshTokenResponse> call = OAuthService.getRefreshToken(properties.getProperty(Constants.CLIENT_ID),
                properties.getProperty(Constants.CLIENT_SECRET), "refresh_token", accessToken.refreshToken());

        try {
            RefreshTokenResponse refreshToken = call.execute().body();

            if (refreshToken == null) {
                throw new IllegalStateException("Refresh token is null");
            }

            var updatedAccessToken = new AccessToken(refreshToken.accessToken(), accessToken.refreshToken(),
                    LocalDateTime.now().plusSeconds(refreshToken.expiresIn()).toString());

            writeSerializedToken(updatedAccessToken);

            return refreshToken.accessToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
