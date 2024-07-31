package org.wentura.calendar;

import static org.wentura.calendar.util.TimeUtils.getFormattedDuration;

import org.wentura.calendar.data.event.EventRepository;
import org.wentura.calendar.util.ArgumentParser;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class App {

    static void run(String[] args) {
        var yearMonth = ArgumentParser.parse(args);
        var eventToTime = getEventToTimeMap(yearMonth);

        printEvents(yearMonth, eventToTime);

        System.exit(0);
    }

    private static Map<String, Long> getEventToTimeMap(YearMonth yearMonth) {
        var eventRepository = new EventRepository();
        var events = eventRepository.getEventsFromPrimaryCalendar(yearMonth);

        var eventToTime = new HashMap<String, Long>();

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
            System.out.println("None");
        } else {
            for (var pair : eventToTime.entrySet()) {
                var totalSeconds = pair.getValue();

                System.out.println(
                        "  " + pair.getKey() + ": " + getFormattedDuration(totalSeconds));
            }
        }
    }
}
