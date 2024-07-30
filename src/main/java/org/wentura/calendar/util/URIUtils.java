package org.wentura.calendar.util;

import java.util.HashMap;
import java.util.Map;

public class URIUtils {

    public static Map<String, String> queryToMap(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }

        var result = new HashMap<String, String>();

        for (var param : query.split("&")) {
            var entry = param.split("=");

            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }

        return result;
    }
}
