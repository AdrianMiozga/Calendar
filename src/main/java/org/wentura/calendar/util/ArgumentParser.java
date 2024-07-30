package org.wentura.calendar.util;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class ArgumentParser {

    public static YearMonth parse(String[] args) {
        if (args.length == 0) {
            return YearMonth.now();
        }

        if (containsHelpOption(args)) {
            printHelp();
            System.exit(0);
            return null;
        } else {
            return getYearMonth(args);
        }
    }

    private static boolean containsHelpOption(String[] args) {
        for (var arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg) || "help".equals(arg)) {
                return true;
            }
        }

        return false;
    }

    private static void printHelp() {
        String programName = "calendar.exe";

        System.out.println("Retrieves events from Google Calendar and sums their durations");
        System.out.println("for the specified month or the current month if no date is provided.");
        System.out.println();
        System.out.println("Usage: " + programName + " [YEAR-MONTH]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println(
                "  [YEAR-MONTH]    The year and month for which to sum event durations, in the");
        System.out.println("                  format YYYY-MM. If not provided, the current month is used.");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  " + programName);
        System.out.println("  " + programName + " 2024-06");
    }

    private static YearMonth getYearMonth(String[] args) {
        try {
            return YearMonth.parse(args[0]);
        } catch (DateTimeParseException exception) {
            System.out.println("Couldn't parse date");
            System.exit(1);
            return null;
        }
    }
}
