package org.example;

import org.example.data.CalendarRepository;
import org.example.data.Event;
import org.example.data.EventResponse;
import retrofit2.Response;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;


public class Main {

    public static void main(String[] args) {
        CalendarRepository calendarRepository = new CalendarRepository();

        Response<EventResponse> response = calendarRepository.getEvents();
        EventResponse eventResponse = response.body();

        if (eventResponse == null) {
            throw new IllegalStateException("eventResponse is null");
        }

        HashMap<String, Long> eventToTime = new HashMap<>();

        for (Event event : eventResponse.events()) {
            OffsetDateTime start = OffsetDateTime.parse(event.start().offsetDateTime());
            OffsetDateTime end = OffsetDateTime.parse(event.end().offsetDateTime());

            Duration duration = Duration.between(start, end);

            if (eventToTime.containsKey(event.title())) {
                long updatedDuration = eventToTime.get(event.title()) + duration.getSeconds();

                eventToTime.put(event.title(), updatedDuration);
            } else {
                eventToTime.put(event.title(), duration.getSeconds());
            }
        }

        for (var pair : eventToTime.entrySet()) {
            long totalSeconds = pair.getValue();

            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;

            String formattedDuration = String.format("%dh %dm", hours, minutes);
            System.out.println(pair.getKey() + ": " + formattedDuration);
        }
    }
}
