package org.example.data;

import org.example.api.CalendarService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;


public class CalendarRepository {

    public static final String BASE_URL = "https://www.googleapis.com/calendar/v3/";

    private final CalendarService calendarService = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalendarService.class);

    public Response<EventResponse> getEventsFromPrimaryCalendar(String accessToken) {
        Call<EventResponse> call = calendarService.getEvents("Bearer " + accessToken);

        try {
            return call.execute();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
