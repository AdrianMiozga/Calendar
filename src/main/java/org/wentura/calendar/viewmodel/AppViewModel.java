package org.wentura.calendar.viewmodel;

import org.wentura.calendar.data.event.EventRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppViewModel {

    private final EventRepository eventRepository;

    public AppViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Map<String, Long> getEventToTimeMap(YearMonth yearMonth) {
        var events = eventRepository.getEventsFromPrimaryCalendar(yearMonth);

        var eventToTime = new LinkedHashMap<String, Long>();

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

        var entryList = new ArrayList<>(eventToTime.entrySet());

        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        var sortedMap = new LinkedHashMap<String, Long>();

        for (var entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
