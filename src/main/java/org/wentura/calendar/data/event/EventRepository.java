package org.wentura.calendar.data.event;

import static org.wentura.calendar.config.Constants.*;
import static org.wentura.calendar.util.TimeUtils.getFirstDayOfCurrentMonth;
import static org.wentura.calendar.util.TimeUtils.getFirstDayOfNextMonth;

import static java.net.HttpURLConnection.*;

import org.wentura.calendar.api.EventService;
import org.wentura.calendar.data.oauth2.OAuthRepository;
import org.wentura.calendar.util.UnauthorizedException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

public class EventRepository {

    private static final OAuthRepository OAuthRepository = new OAuthRepository();

    public static final String BASE_URL = "https://www.googleapis.com/calendar/v3/";

    private final EventService eventService =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(EventService.class);

    public List<Event> getEventsFromPrimaryCalendar(YearMonth yearMonth) {
        var firstDayOfCurrentMonth = getFirstDayOfCurrentMonth(yearMonth);
        var firstDayOfNextMonth = getFirstDayOfNextMonth(yearMonth);

        var retryCount = 0;

        while (retryCount < MAX_HTTP_RETRIES) {
            try {
                return fetchEvents(firstDayOfCurrentMonth, firstDayOfNextMonth);
            } catch (UnauthorizedException exception) {
                OAuthRepository.deleteSerializedToken();
                retryCount++;
            } catch (IOException exception) {
                retryCount++;
            }
        }

        throw new RuntimeException("Failed to fetch events after " + MAX_HTTP_RETRIES + " retries");
    }

    private List<Event> fetchEvents(String startOffsetDateTime, String endOffsetDateTime)
            throws IOException, UnauthorizedException {
        var call =
                eventService.getEvents(
                        "Bearer " + OAuthRepository.getAccessToken(),
                        startOffsetDateTime,
                        endOffsetDateTime);

        var response = call.execute();

        if (response.code() == HTTP_UNAUTHORIZED) {
            throw new UnauthorizedException();
        }

        if (!response.isSuccessful()) {
            throw new RuntimeException("Request failed with code: " + response.code());
        }

        var eventResponse = response.body();

        if (eventResponse == null) {
            throw new IllegalStateException("Event response is null");
        }

        return eventResponse.events();
    }
}
