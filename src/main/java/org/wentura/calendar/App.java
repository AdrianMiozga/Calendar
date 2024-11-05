package org.wentura.calendar;

import static org.wentura.calendar.util.TimeUtils.getFormattedDuration;

import org.wentura.calendar.data.event.DefaultEventRepository;
import org.wentura.calendar.data.eventtime.EventTime;
import org.wentura.calendar.util.ArgumentParser;
import org.wentura.calendar.viewmodel.AppViewModel;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class App {

    static void run(String[] args) {
        var yearMonth = ArgumentParser.parse(args);
        var appViewModel = new AppViewModel(new DefaultEventRepository());
        var eventToTime = appViewModel.getEventTime(yearMonth);

        printEvents(yearMonth, eventToTime);

        System.exit(0);
    }

    private static void printEvents(YearMonth yearMonth, List<EventTime> eventTimeList) {
        System.out.println(
                "Showing events from "
                        + yearMonth.format(
                                DateTimeFormatter.ofPattern("MMMM y").withLocale(Locale.US))
                        + ": ");

        if (eventTimeList.isEmpty()) {
            System.out.println("None");
        } else {
            for (var eventTime : eventTimeList) {
                System.out.println(
                        "  "
                                + eventTime.eventName()
                                + ": "
                                + getFormattedDuration(eventTime.durationInSeconds()));
            }
        }
    }
}
