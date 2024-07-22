package org.wentura.calendar.api;

import org.wentura.calendar.data.event.EventResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface EventService {

    @GET("calendars/primary/events")
    Call<EventResponse> getEvents(
            @Header("Authorization") String authorization,
            @Query("timeMin") String startOffsetDateTime,
            @Query("timeMax") String endOffsetDateTime);
}
