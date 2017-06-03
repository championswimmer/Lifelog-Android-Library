package com.sonymobile.lifelog.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper class for handling a most common subset of ISO 8601 strings
 * (in the following format: "2008-03-01T13:00:00+01:00"). It supports
 * parsing the "Z" timezone, but many other less-used features are
 * missing.
 */
public final class ISO8601Date {

    private static final SimpleDateFormat FROM_CALENDAR_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    private static final SimpleDateFormat TO_CALENDAR_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

    /**
     * Transform Calendar to ISO 8601 string.
     */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();

        synchronized (FROM_CALENDAR_FORMATTER) {
            FROM_CALENDAR_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
            return FROM_CALENDAR_FORMATTER.format(date);
        }
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }


    /**
     * Transform ISO 8601 string to Calendar.
     */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        // if it ends with "Z", replace it.
        String s = iso8601string.replaceAll("Z$", "+00:00");

        // to get rid of the last ":" to convert from ISO 8601 to RFC 822
        int lastIndex = s.lastIndexOf(":");
        s = s.substring(0, lastIndex) + s.substring(lastIndex + 1);

        Date date;
        synchronized (TO_CALENDAR_FORMATTER) {
            date = TO_CALENDAR_FORMATTER.parse(s);
        }

        // TODO Because Date class of Java does not handle timezone, timezone information
        // in calendar is incorrect and system default is used regardless of original String.
        calendar.setTime(date);
        return calendar;
    }
}