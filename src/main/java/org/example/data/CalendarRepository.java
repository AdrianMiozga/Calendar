package org.example.data;

import org.example.api.CalendarService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class CalendarRepository {

    public static final String BASE_URL = "https://www.googleapis.com/calendar/v3/";

    private final CalendarService calendarService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalendarService.class);

    public Response<EventResponse> getEvents() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("oauth2.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Call<EventResponse> calendarList = calendarService.getEvents(properties.getProperty("token"));

        try {
            return calendarList.execute();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
