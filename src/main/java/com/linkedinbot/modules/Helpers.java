package com.linkedinbot.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {

    private static final Logger log = LoggerFactory.getLogger(Helpers.class);
    private static final Random RANDOM = new Random();
    private static String logsFilePath = "logs/log.txt";

    public static void setLogsFilePath(String path) {
        logsFilePath = path;
    }

    public static void buffer(int speed) {
        try {
            if (speed <= 0) return;
            long ms;
            if (speed < 2)      ms = 600 + RANDOM.nextInt(400);
            else if (speed < 3) ms = 1000 + RANDOM.nextInt(800);
            else                ms = 1800 + RANDOM.nextInt(speed * 1000 - 1800);
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void printLog(String message) {
        log.info(message);
        appendToLogFile(message);
    }

    public static void printLog(String message, Exception e) {
        log.error(message, e);
        appendToLogFile("[ERROR] " + message + " | " + e.getMessage());
    }

    private static void appendToLogFile(String message) {
        try {
            File logFile = new File(logsFilePath);
            logFile.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(logFile, true)) {
                fw.write(LocalDateTime.now() + " | " + message + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    public static void makeDirectories(String... paths) {
        for (String path : paths) {
            if (path == null || path.isBlank()) continue;
            File f = new File(path);
            File dir = path.contains(".") ? f.getParentFile() : f;
            if (dir != null && !dir.exists()) dir.mkdirs();
        }
    }

    public static LocalDateTime calculateDatePosted(String timeString) {
        if (timeString == null || timeString.isBlank()) return null;
        Pattern p = Pattern.compile(
            "(\\d+)\\s+(second|minute|hour|day|week|month|year)s?\\s+ago",
            Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(timeString.trim());
        if (m.find()) {
            int value = Integer.parseInt(m.group(1));
            String unit = m.group(2).toLowerCase();
            LocalDateTime now = LocalDateTime.now();
            return switch (unit) {
                case "second" -> now.minus(value, ChronoUnit.SECONDS);
                case "minute" -> now.minus(value, ChronoUnit.MINUTES);
                case "hour"   -> now.minus(value, ChronoUnit.HOURS);
                case "day"    -> now.minus(value, ChronoUnit.DAYS);
                case "week"   -> now.minus(value * 7L, ChronoUnit.DAYS);
                case "month"  -> now.minus(value * 30L, ChronoUnit.DAYS);
                case "year"   -> now.minus(value * 365L, ChronoUnit.DAYS);
                default       -> null;
            };
        }
        return null;
    }

    public static void showAlert(String message, String title) {
        javax.swing.JOptionPane.showMessageDialog(
            null, message, title,
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(String message, String title) {
        int result = javax.swing.JOptionPane.showConfirmDialog(
            null, message, title,
            javax.swing.JOptionPane.YES_NO_OPTION);
        return result == javax.swing.JOptionPane.YES_OPTION;
    }
}
