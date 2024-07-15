package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.config.Constants;
import org.example.data.*;
import org.example.util.Util;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Properties;


public class Main {

    // TODO: Use callback
    private static HttpServer httpServer;
    private static String retrievedCode;

    public static void main(String[] args) throws Exception {
        var properties = new Properties();

        try {
            properties.load(new FileInputStream(Constants.PROPERTIES_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var scope =
                "https://www.googleapis.com/auth/calendar.readonly+https://www.googleapis.com/auth/calendar.events.readonly";
        var uri =
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + properties.getProperty(Constants.CLIENT_ID)
                        + "&redirect_uri=" + properties.getProperty(Constants.REDIRECT_URI)
                        + "&response_type=code&scope=" + scope;

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(uri));
        }

        httpServer = HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty(Constants.PORT))), 0);
        httpServer.createContext(properties.getProperty(Constants.REDIRECT_PATH), new MyHandler());
        httpServer.setExecutor(null);
        httpServer.start();

        synchronized (Main.class) {
            Main.class.wait();
        }

        var OAuthRepository = new OAuthRepository();
        var OAuthResponse = OAuthRepository.getResponse(retrievedCode);

        var token = OAuthResponse.body().accessToken();

        if (token == null) {
            throw new IllegalStateException("Access token is null");
        }

        var calendarRepository = new CalendarRepository();

        var calendarResponse = calendarRepository.getEvents("Bearer " + token);
        var eventResponse = calendarResponse.body();

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

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            var code = Util.queryToMap(httpExchange.getRequestURI().getQuery()).get("code");

            if (code == null) {
                return;
            }

            retrievedCode = code;

            var response = "<p>You can close this page</p>";
            httpExchange.sendResponseHeaders(200, response.length());

            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();

            httpServer.stop(0);

            synchronized (Main.class) {
                Main.class.notify();
            }
        }
    }
}
