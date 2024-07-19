package org.example;

import org.example.data.event.EventRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;

public class App {

    private static final EventRepository eventRepository = new EventRepository();

    static void run(String[] args) {
        YearMonth yearMonth;

        if (args.length > 0) {
            try {
                yearMonth = YearMonth.parse(args[0]);
            } catch (DateTimeParseException exception) {
                System.out.println("Couldn't parse date");
                throw new RuntimeException(exception);
            }
        } else {
            yearMonth = YearMonth.now();
        }

        var events = eventRepository.getEventsFromPrimaryCalendar(yearMonth);

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

        System.out.println(
                "Showing events from "
                        + yearMonth.format(
                                DateTimeFormatter.ofPattern("MMMM y").withLocale(Locale.US))
                        + ": ");

        if (eventToTime.isEmpty()) {
            System.out.println("Not found");
        } else {
            for (var pair : eventToTime.entrySet()) {
                var totalSeconds = pair.getValue();

                var hours = totalSeconds / 3600;
                var minutes = (totalSeconds % 3600) / 60;

                var formattedDuration = String.format("%dh %dm", hours, minutes);
                System.out.println("  " + pair.getKey() + ": " + formattedDuration);
            }
        }

        System.exit(0);
    }
}
