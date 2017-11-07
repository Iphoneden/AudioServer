package ru.home.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.home.common.StringUtils.DateFormat.*;

public class StringUtils {

    public static String repeat(String str, int count) {
        return String.format("%0" + count + "d", 0).replace("0",str);
    }

    public enum DateFormat {
        DATA_FORMAT_SHORT("dd.MM"),
        DATE_TIME_FORMAT_SHORT("dd.MM HH.mm"),
        TIME_FORMAT_SHORT("HH.mm"),

        DATA_FORMAT("dd.MM.yy"),
        DATE_TIME_FORMAT("dd.MM.yy HH.mm.ss"),
        TIME_FORMAT("HH.mm.ss"),
        TIME_FORMAT_HIGH("mm.ss.SSS"),

        DATA_FORMAT_LONG("dd.MM.yyyy"),
        DATE_TIME_FORMAT_LONG("dd.MM.yyyy HH.mm.ss");

        private String format;

        DateFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    public static String toDate(Date date) {
        return toDate(date, DATA_FORMAT);
    }

    public static String toTime(Date date) {
        return toDate(date, TIME_FORMAT);
    }

    public static String toDateTime(Date date) {
        return toDate(date, DATE_TIME_FORMAT);
    }

    public static String toTimeHigh(Date date) {
        return toDate(date, TIME_FORMAT_HIGH);
    }

    public static String toDate(Date date, DateFormat dateFormat) {
        return toDate(date, dateFormat.getFormat());
    }

    public static String toDate(Date date, String format) {
        return date == null ? null : new SimpleDateFormat(format).format(date);
    }

    public static boolean isNoEmpty(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static String trim(String string) {
        return string != null ? string.trim() : null;
    }

    /**
     * заменить без учета регистр
     */
    public static String replaceAll(String string, String regex, String replacement) {
        return string != null ? string.replaceAll("(?i)" + regex, replacement) : null;
    }

    public static String durationToString(double longRec) {
        int seconds = (int) (longRec) % 60;
        int minutes = (int) ((longRec / 60) % 60);
        int hours = (int) ((longRec / (60 * 60)) % 24);
        return String.format("%02d.%02d.%02d", hours, minutes, seconds);
    }

    public static String durationToShortString(double longRec) {
        int seconds = (int) (longRec) % 60;
        int micro = (int) ((longRec - seconds) * 1000);
        int minutes = (int) ((longRec / 60) % 60);
        int hours = (int) ((longRec / (60 * 60)) % 24);
        if (hours > 0) {
            return String.format("%02d.%02d.%02d", hours, minutes, seconds);
        }
        if (minutes > 0) {
            return String.format(".%02d.%02d", minutes, seconds);
        }
        if (seconds > 0 && seconds < 3) {
            return String.format("..%02d.%02d", seconds, micro/10);
        }
        if (seconds > 0) {
            return String.format("..%02d.%01d", seconds, micro/100);
        }
        return String.format(".%03d", micro);
    }

}
