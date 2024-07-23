package org.wentura.calendar;

import org.wentura.calendar.data.event.EventRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.wentura.calendar.util.Util.*;

public class App {

    static void run(String[] args) {
        var yearMonth = parseArguments(args);
        var eventToTime = getEventToTimeMap(yearMonth);

        printEvents(yearMonth, eventToTime);

        System.exit(0);
    }

    private static YearMonth parseArguments(String[] args) {
        YearMonth yearMonth;

        if (args.length > 0) {
            try {
                yearMonth = YearMonth.parse(args[0]);
            } catch (DateTimeParseException exception) {
                System.out.println("Couldn't parse date");
                System.exit(1);
                return null;
            }
        } else {
            yearMonth = YearMonth.now();
        }

        return yearMonth;
    }

    private static Map<String, Long> getEventToTimeMap(YearMonth yearMonth) {
        var eventRepository = new EventRepository();
        var events = eventRepository.getEventsFromPrimaryCalendar(yearMonth);

        Map<String, Long> eventToTime = new HashMap<>();

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

        return eventToTime;
    }

    private static void printEvents(YearMonth yearMonth, Map<String, Long> eventToTime) {
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
    }
}
