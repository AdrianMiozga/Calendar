package org.wentura.calendar.data.event;

import org.wentura.calendar.api.EventService;
import org.wentura.calendar.data.oauth2.OAuthRepository;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventRepository {

    public static final String BASE_URL = "https://www.googleapis.com/calendar/v3/";

    private static final org.wentura.calendar.data.oauth2.OAuthRepository OAuthRepository =
            new OAuthRepository();
    private final EventService eventService =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(EventService.class);

    public List<Event> getEventsFromPrimaryCalendar(YearMonth yearMonth) {
        String startOffsetDateTime =
                yearMonth
                        .atDay(1)
                        .atStartOfDay()
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_DATE_TIME);

        String endOffsetDateTime =
                yearMonth
                        .plusMonths(1)
                        .atDay(1)
                        .atStartOfDay()
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_DATE_TIME);

        Call<EventResponse> call =
                eventService.getEvents(
                        "Bearer " + OAuthRepository.getAccessToken(),
                        startOffsetDateTime,
                        endOffsetDateTime);

        try {
            var eventResponse = call.execute().body();

            if (eventResponse == null) {
                throw new IllegalStateException("eventResponse is null");
            }

            return eventResponse.events();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
