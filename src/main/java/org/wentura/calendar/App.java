package org.wentura.calendar;

import org.wentura.calendar.data.event.EventRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;

import static org.wentura.calendar.util.Util.*;

public class App {

    private static final EventRepository eventRepository = new EventRepository();

    static void run(String[] args) {
        YearMonth yearMonth;

        if (args.length > 0) {
            try {
                yearMonth = YearMonth.parse(args[0]);
            } catch (DateTimeParseException exception) {
                System.out.println("Couldn't parse date");
                System.exit(1);
                return;
            }
        } else {
            yearMonth = YearMonth.now();
        }

        var events = eventRepository.getEventsFromPrimaryCalendar(yearMonth);

        HashMap<String, Long> eventToTime = new HashMap<>();

        for (var event : events) {
            if (event.start().offsetDateTime() == null || event.end().offsetDateTime() == null) {
                continue;
            }

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

                System.out.println(
                        "  " + pair.getKey() + ": " + getFormattedDuration(totalSeconds));
            }
        }

        System.exit(0);
    }
}
