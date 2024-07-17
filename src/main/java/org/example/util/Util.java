package org.example.util;

import org.example.config.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Util {

    public static Map<String, String> queryToMap(String query) {
        if (query == null) {
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

    public static Properties getAppProperties() {
        var properties = new Properties();

        try {
            properties.load(new FileInputStream(Constants.PROPERTIES_FILE));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return properties;
    }
}
