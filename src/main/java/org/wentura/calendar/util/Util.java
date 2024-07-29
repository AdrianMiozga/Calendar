package org.wentura.calendar.util;

import java.util.HashMap;
import java.util.Map;

public class Util {

    public static Map<String, String> queryToMap(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }

        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String[] entry = param.split("=");

            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }

        return result;
    }

    public static String getFormattedDuration(Long totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Seconds can't be negative");
        }

        var hours = totalSeconds / 3600;
        var minutes = (totalSeconds % 3600) / 60;

        return String.format("%dh %dm", hours, minutes);
    }
}
