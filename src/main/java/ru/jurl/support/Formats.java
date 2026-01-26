package ru.jurl.support;

public class Formats {
    public static String duration(long millis) {
        long seconds = millis / 1000;
        long ms = millis % 1000;
        long SS = seconds % 60;
        long MM = (seconds % 3600) / 60;
        long HH = seconds / 3600;
        return String.format("%02d:%02d:%02d.%03d", HH, MM, SS, ms);
    }
}
