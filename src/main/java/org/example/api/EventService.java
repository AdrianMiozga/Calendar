package org.example.api;

import org.example.data.event.EventResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface EventService {

    @GET("calendars/primary/events")
    Call<EventResponse> getEvents(@Header("Authorization") String authorization);
}
