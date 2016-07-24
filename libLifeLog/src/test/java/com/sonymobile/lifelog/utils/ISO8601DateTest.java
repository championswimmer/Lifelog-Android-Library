package com.sonymobile.lifelog.utils;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;

public class ISO8601DateTest {
    private static final String TEST_TIME_TOKYO = "2016-07-17T16:11:18.640+09:00";
    private static final String TEST_TIME_LONDON = "2016-07-17T16:11:18.640Z";

    @Test
    public void testParseString() throws ParseException {
        Calendar tokyoCalendar = ISO8601Date.toCalendar(TEST_TIME_TOKYO);
        tokyoCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

        assertEquals(Calendar.JULY, tokyoCalendar.get(Calendar.MONTH));
        assertEquals(17, tokyoCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(16, tokyoCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(11, tokyoCalendar.get(Calendar.MINUTE));
        assertEquals(18, tokyoCalendar.get(Calendar.SECOND));
        assertEquals(640, tokyoCalendar.get(Calendar.MILLISECOND));


        Calendar londonCalendar = ISO8601Date.toCalendar(TEST_TIME_LONDON);
        londonCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        assertEquals(Calendar.JULY, londonCalendar.get(Calendar.MONTH));
        assertEquals(17, londonCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(16, londonCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(11, londonCalendar.get(Calendar.MINUTE));
        assertEquals(18, londonCalendar.get(Calendar.SECOND));
        assertEquals(640, londonCalendar.get(Calendar.MILLISECOND));
    }
}
