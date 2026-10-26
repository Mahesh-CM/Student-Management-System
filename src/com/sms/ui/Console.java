package com.sms.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Small helper for validated console input. */
public final class Console {

    private static final Scanner IN = new Scanner(System.in);

    private Console() { }

    public static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String s = IN.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  Value is required.");
        }
    }

    /** Returns the current value when the user just presses Enter. */
    public static String readOptional(String prompt, String current) {
        System.out.print(prompt + " [" + (current == null ? "" : current) + "]: ");
        String s = IN.nextLine().trim();
        return s.isEmpty() ? current : s;
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Integer.parseInt(IN.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    public static int readInt(String prompt, int current) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String s = IN.nextLine().trim();
            if (s.isEmpty()) return current;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Double.parseDouble(IN.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number.");
            }
        }
    }

    /** Date in yyyy-MM-dd format. */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(IN.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("  Use the format yyyy-MM-dd, e.g. 2005-03-14.");
            }
        }
    }

    public static LocalDate readDate(String prompt, LocalDate current) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd) [" + (current == null ? "" : current) + "]: ");
            String s = IN.nextLine().trim();
            if (s.isEmpty()) return current;
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("  Use the format yyyy-MM-dd.");
            }
        }
    }

    public static boolean confirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        return IN.nextLine().trim().equalsIgnoreCase("y");
    }
}
