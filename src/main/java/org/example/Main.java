package org.example;

import org.example.data.CalendarRepository;
import org.example.data.Event;
import org.example.data.EventResponse;
import retrofit2.Response;

import java.time.Duration;
import java.time.OffsetDateTime;


public class Main {

    public static void main(String[] args) {
        CalendarRepository calendarRepository = new CalendarRepository();

        Response<EventResponse> response = calendarRepository.getEvents();
        EventResponse eventResponse = response.body();

        if (eventResponse == null) {
            return;
        }

        long totalSeconds = 0;

        for (Event event : eventResponse.items()) {
            OffsetDateTime start = OffsetDateTime.parse(event.start().dateTime());
            OffsetDateTime end = OffsetDateTime.parse(event.end().dateTime());

            Duration duration = Duration.between(start, end);

            totalSeconds += duration.getSeconds();
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String formattedDuration = String.format("%dh %dm %ds", hours, minutes, seconds);
        System.out.println("Total for all events: " + formattedDuration);
    }
}
