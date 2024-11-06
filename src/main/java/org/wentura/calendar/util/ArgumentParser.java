package org.wentura.calendar.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

public class ArgumentParser {

    private static List<String> arguments;

    public static YearMonth parse(String[] args) {
        if (args.length == 0) {
            return YearMonth.now();
        }

        arguments = new LinkedList<>(List.of(args));

        if (containsHelpOption()) {
            printHelp();
            System.exit(0);
            return null;
        } else {
            var loggerLevel = loggerLevelOption();

            if (loggerLevel != null) {
                var root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
                root.addAppender(getConsoleAppender(loggerLevel));
            }

            return getYearMonth();
        }
    }

    private static boolean containsHelpOption() {
        return arguments.contains("--help") || arguments.contains("-h") || arguments.contains("help");
    }

    private static void printHelp() {
        var programName = "calendar";

        System.out.println("Retrieves events from Google Calendar and sums their durations for the specified");
        System.out.println("month or the current month if no date is provided.");
        System.out.println();
        System.out.println("Usage: " + programName + " YEAR-MONTH [OPTIONS]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println(
                "  YEAR-MONTH       The year and month for which to sum event durations, in the");
        System.out.println(
                "                   format YYYY-MM. If not provided, the current month is used.");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help       Print this help text and exit.");
        System.out.println("  --debug, --info  Print various debug/info messages.");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  " + programName);
        System.out.println("  " + programName + " 2024-06");
    }

    private static String loggerLevelOption() {
        for (var argument : arguments) {
            if ("--info".equals(argument) || "--debug".equals(argument)) {
                arguments.remove(argument);
                return argument.substring(2);
            }
        }

        return null;
    }

    private static ConsoleAppender<ILoggingEvent> getConsoleAppender(String loggerLevel) {
        var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        var patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.start();

        var consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(patternLayoutEncoder);

        var filter = new ThresholdFilter();
        filter.setLevel(loggerLevel);
        filter.start();
        consoleAppender.addFilter(filter);

        consoleAppender.start();
        return consoleAppender;
    }

    private static YearMonth getYearMonth() {
        try {
            return YearMonth.parse(arguments.getFirst());
        } catch (DateTimeParseException exception) {
            System.out.println("Couldn't parse date");
            System.exit(1);
            return null;
        }
    }
}
