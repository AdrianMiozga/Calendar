package org.wentura.calendar;

import static org.wentura.calendar.util.TimeUtils.getFormattedDuration;

import org.wentura.calendar.data.event.DefaultEventRepository;
import org.wentura.calendar.util.ArgumentParser;
import org.wentura.calendar.viewmodel.AppViewModel;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class App {

    static void run(String[] args) {
        var yearMonth = ArgumentParser.parse(args);
        var appViewModel = new AppViewModel(new DefaultEventRepository());
        var eventToTime = appViewModel.getEventToTimeMap(yearMonth);

        printEvents(yearMonth, eventToTime);

        System.exit(0);
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
