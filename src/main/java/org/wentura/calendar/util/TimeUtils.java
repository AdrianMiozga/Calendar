package org.wentura.calendar.util;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String getFormattedDuration(Long totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Seconds can't be negative");
        }

        var hours = totalSeconds / 3600;
        var minutes = (totalSeconds % 3600) / 60;

        return String.format("%dh %dm", hours, minutes);
    }

    public static String getFirstDayOfCurrentMonth(YearMonth yearMonth) {
        return yearMonth
                .atDay(1)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static String getFirstDayOfNextMonth(YearMonth yearMonth) {
        return yearMonth
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
