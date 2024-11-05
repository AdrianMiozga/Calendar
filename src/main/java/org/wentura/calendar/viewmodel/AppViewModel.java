package org.wentura.calendar.viewmodel;

import org.wentura.calendar.data.event.EventRepository;
import org.wentura.calendar.data.eventtime.EventTime;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class AppViewModel {

    private final EventRepository eventRepository;

    public AppViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventTime> getEventTime(YearMonth yearMonth) {
        var events = eventRepository.getEventsFromPrimaryCalendar(yearMonth);

        var eventToTime = new LinkedHashMap<String, Long>();

        for (var event : events) {
            if (event.start().offsetDateTime() == null || event.end().offsetDateTime() == null) {
                continue;
            }

            var start = OffsetDateTime.parse(event.start().offsetDateTime());
            var end = OffsetDateTime.parse(event.end().offsetDateTime());

            var durationInSeconds = Duration.between(start, end).getSeconds();

            eventToTime.merge(event.title(), durationInSeconds, Long::sum);
        }

        return eventToTime.entrySet().stream()
                .map(entry -> new EventTime(entry.getKey(), entry.getValue()))
                .sorted()
                .collect(Collectors.toList());
    }
}
