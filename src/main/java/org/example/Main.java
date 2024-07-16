package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.config.Constants;
import org.example.data.AccessToken;
import org.example.data.CalendarRepository;
import org.example.data.OAuthRepository;
import org.example.util.Util;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public class Main {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static HttpServer httpServer;
    private static String authorizationCode;

    public static void main(String[] args) throws Exception {
        AccessToken accessToken = null;

        if (Files.exists(Paths.get(Constants.ACCESS_TOKEN_FILE))) {
            try (FileInputStream fileIn = new FileInputStream(Constants.ACCESS_TOKEN_FILE);
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {
                accessToken = (AccessToken) in.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        if (accessToken == null || LocalDateTime.parse(accessToken.dateTimeExpires()).isBefore(LocalDateTime.now())) {
            var properties = new Properties();

            try {
                properties.load(new FileInputStream(Constants.PROPERTIES_FILE));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            var scopes =
                    "https://www.googleapis.com/auth/calendar.readonly+https://www.googleapis.com/auth/calendar.events.readonly";
            var uri = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + properties.getProperty(
                    Constants.CLIENT_ID) + "&redirect_uri=" + properties.getProperty(Constants.REDIRECT_URI)
                    + "&response_type=code&scope=" + scopes;

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(uri));
            }

            httpServer =
                    HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty(Constants.PORT))),
                            0);

            httpServer.createContext(properties.getProperty(Constants.REDIRECT_PATH), httpExchange -> {
                var code = Util.queryToMap(httpExchange.getRequestURI().getQuery()).get("code");

                if (code == null) {
                    return;
                }

                authorizationCode = code;

                var response = "<p>You can close this page</p>";
                httpExchange.sendResponseHeaders(200, response.length());

                OutputStream outputStream = httpExchange.getResponseBody();
                outputStream.write(response.getBytes());
                outputStream.close();

                httpServer.stop(0);

                latch.countDown();
            });

            httpServer.setExecutor(null);
            httpServer.start();

            latch.await();

            var OAuthRepository = new OAuthRepository();
            var OAuthResponse = OAuthRepository.getAccessToken(authorizationCode).body();

            if (OAuthResponse == null) {
                throw new IllegalStateException("Request body is null");
            }

            var temp = new AccessToken(OAuthResponse.accessToken(),
                    LocalDateTime.now().plusSeconds(OAuthResponse.expiresIn()).toString());
            accessToken = temp;

            try (FileOutputStream fileOut = new FileOutputStream(Constants.ACCESS_TOKEN_FILE);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(temp);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            var token = OAuthResponse.accessToken();

            if (token == null) {
                throw new IllegalStateException("Access token is null");
            }
        }

        var calendarRepository = new CalendarRepository();
        var eventResponse = calendarRepository.getEventsFromPrimaryCalendar(accessToken.accessToken()).body();

        if (eventResponse == null) {
            throw new IllegalStateException("eventResponse is null");
        }

        HashMap<String, Long> eventToTime = new HashMap<>();

        for (var event : eventResponse.events()) {
            var start = OffsetDateTime.parse(event.start().offsetDateTime());
            var end = OffsetDateTime.parse(event.end().offsetDateTime());

            var duration = Duration.between(start, end);

            if (eventToTime.containsKey(event.title())) {
                var updatedDuration = eventToTime.get(event.title()) + duration.getSeconds();

                eventToTime.put(event.title(), updatedDuration);
            } else {
                eventToTime.put(event.title(), duration.getSeconds());
            }
        }

        for (var pair : eventToTime.entrySet()) {
            var totalSeconds = pair.getValue();

            var hours = totalSeconds / 3600;
            var minutes = (totalSeconds % 3600) / 60;

            var formattedDuration = String.format("%dh %dm", hours, minutes);
            System.out.println(pair.getKey() + ": " + formattedDuration);
        }
    }
}
