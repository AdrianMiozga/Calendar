package org.wentura.calendar.data.config;

import com.google.gson.Gson;

import org.wentura.calendar.config.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class ConfigRepository {

    private static Config config;

    public static Config getAppConfig() {
        if (config != null) {
            return config;
        }

        try (var reader = Files.newBufferedReader(Paths.get(Constants.CONFIG_FILENAME))) {
            var gson = new Gson();
            config = gson.fromJson(reader, Config.class);
            return config;
        } catch (NoSuchFileException exception) {
            System.out.println("No configuration file (" + Constants.CONFIG_FILENAME + ")");
            System.exit(1);
            return null;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
