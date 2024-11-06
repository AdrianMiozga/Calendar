package org.wentura.calendar.data.oauth2;

import com.sun.net.httpserver.HttpServer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wentura.calendar.api.OAuthService;
import org.wentura.calendar.config.Constants;
import org.wentura.calendar.data.config.Config;
import org.wentura.calendar.data.config.ConfigRepository;
import org.wentura.calendar.util.URIUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.awt.*;
import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

public class OAuthRepository {

    public static final String BASE_URL = "https://oauth2.googleapis.com/";

    private final Logger logger = LoggerFactory.getLogger(OAuthRepository.class);
    private final HttpLoggingInterceptor interceptor =
            new HttpLoggingInterceptor(logger::debug)
                    .setLevel(HttpLoggingInterceptor.Level.BODY);
    private final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    private final OAuthService OAuthService =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(OAuthService.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    private HttpServer httpServer;
    private String authorizationCode;
    private final Config config = ConfigRepository.getAppConfig();

    private static boolean isTokenExpired(AccessToken accessToken) {
        return LocalDateTime.parse(accessToken.dateTimeExpires()).isBefore(LocalDateTime.now());
    }

    private static AccessToken readSerializedToken() {
        AccessToken accessToken = null;

        if (Files.exists(Paths.get(Constants.ACCESS_TOKEN_FILENAME))) {
            try (var fileIn = new FileInputStream(Constants.ACCESS_TOKEN_FILENAME);
                    var in = new ObjectInputStream(fileIn)) {
                accessToken = (AccessToken) in.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        return accessToken;
    }

    private static void writeSerializedToken(AccessToken accessToken) {
        try (var fileOut = new FileOutputStream(Constants.ACCESS_TOKEN_FILENAME);
                var out = new ObjectOutputStream(fileOut)) {
            out.writeObject(accessToken);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void deleteSerializedToken() {
        try {
            Files.deleteIfExists(Paths.get(Constants.ACCESS_TOKEN_FILENAME));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getAccessToken() {
        var accessToken = readSerializedToken();

        if (accessToken == null) {
            var authorizationCode = getAuthorizationCode();

            logger.info("Getting access token");
            var call =
                    OAuthService.getAccessToken(
                            authorizationCode,
                            config.clientId(),
                            config.clientSecret(),
                            config.redirectURI(),
                            "authorization_code");

            AccessTokenResponse accessTokenResponse;

            try {
                accessTokenResponse = call.execute().body();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            if (accessTokenResponse == null) {
                throw new IllegalStateException("Access token is null");
            }

            var expirationDate =
                    LocalDateTime.now().plusSeconds(accessTokenResponse.expiresIn()).toString();
            logger.info("Access token will expire {}", expirationDate);

            var newAccessToken =
                    new AccessToken(
                            accessTokenResponse.accessToken(),
                            accessTokenResponse.refreshToken(),
                            expirationDate);

            writeSerializedToken(newAccessToken);

            return newAccessToken.accessToken();
        } else if (isTokenExpired(accessToken)) {
            return getNewAccessToken();
        } else {
            logger.info("Reusing access token");
            return accessToken.accessToken();
        }
    }

    private String getAuthorizationCode() {
        logger.info("Getting authorization code");

        var scopes = new String[] {"https://www.googleapis.com/auth/calendar.events.readonly"};

        var uri =
                "https://accounts.google.com/o/oauth2/v2/auth?client_id="
                        + config.clientId()
                        + "&redirect_uri="
                        + config.redirectURI()
                        + "&response_type=code&scope="
                        + String.join("+", scopes)
                        + "&access_type=offline"
                        + "&prompt=consent";

        try {
            var inetSocketAddress = new InetSocketAddress(config.getHost(), config.getPort());
            logger.info(
                    "Starting web server at {}:{}",
                    inetSocketAddress.getHostName(),
                    inetSocketAddress.getPort());
            httpServer = HttpServer.create(inetSocketAddress, 0);
        } catch (BindException exception) {
            System.out.println("Some process is already listening at port " + config.getPort());
            System.exit(1);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        if (Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                logger.info("Launching browser");
                Desktop.getDesktop().browse(new URI(uri));
            } catch (URISyntaxException | IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        httpServer.createContext(
                config.getRedirectPath(),
                httpExchange -> {
                    var code =
                            URIUtils.queryToMap(httpExchange.getRequestURI().getQuery())
                                    .get("code");

                    if (code == null) {
                        throw new IllegalStateException("Code parameter not found");
                    }

                    logger.info("Retrieved authorization code");
                    authorizationCode = code;

                    try (var inputStream =
                            OAuthRepository.class.getResourceAsStream("/index.html")) {
                        if (inputStream == null) {
                            throw new FileNotFoundException("No index.html");
                        }

                        var response = new String(inputStream.readAllBytes());

                        httpExchange.sendResponseHeaders(200, response.length());

                        OutputStream outputStream = httpExchange.getResponseBody();
                        outputStream.write(response.getBytes());
                        outputStream.close();
                    }

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
        logger.info("Getting new access token through refresh token");

        var accessToken = readSerializedToken();

        if (accessToken == null) {
            throw new IllegalStateException("Access token is null");
        }

        if (accessToken.refreshToken() == null) {
            throw new IllegalStateException("Refresh token is null");
        }

        var call =
                OAuthService.getRefreshToken(
                        config.clientId(),
                        config.clientSecret(),
                        "refresh_token",
                        accessToken.refreshToken());

        try {
            var refreshToken = call.execute().body();

            if (refreshToken == null) {
                throw new IllegalStateException("Refresh token API failed");
            }

            var updatedAccessToken =
                    new AccessToken(
                            refreshToken.accessToken(),
                            accessToken.refreshToken(),
                            LocalDateTime.now().plusSeconds(refreshToken.expiresIn()).toString());

            writeSerializedToken(updatedAccessToken);

            return refreshToken.accessToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
