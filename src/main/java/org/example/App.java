package org.example;

import org.example.data.event.EventRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class App {

    private static final EventRepository eventRepository = new EventRepository();

    static void run() {
        var events = eventRepository.getEventsFromPrimaryCalendar();

        HashMap<String, Long> eventToTime = new HashMap<>();

        for (var event : events) {
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

        System.exit(0);
    }
}
