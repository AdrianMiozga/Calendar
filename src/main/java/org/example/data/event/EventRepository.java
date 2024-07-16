package org.example.data.event;

import org.example.api.EventService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;


public class EventRepository {

    public static final String BASE_URL = "https://www.googleapis.com/calendar/v3/";

    private final EventService eventService = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventService.class);

    public List<Event> getEventsFromPrimaryCalendar(String accessToken) {
        Call<EventResponse> call = eventService.getEvents("Bearer " + accessToken);

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
