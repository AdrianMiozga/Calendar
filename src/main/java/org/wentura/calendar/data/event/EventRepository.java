package org.wentura.calendar.data.event;

import java.time.YearMonth;
import java.util.List;

public interface EventRepository {
    List<Event> getEventsFromPrimaryCalendar(YearMonth yearMonth);
}
