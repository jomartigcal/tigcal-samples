package com.tigcal.wedding;

import java.util.Calendar;

public class Wedding {
    public static final String WEBSITE = "https://wedding.tigcal.com";

    private Wedding() {

    }

    public static String getCountdown() {
        Calendar weddingCalendar = Calendar.getInstance();
        weddingCalendar.set(2019, Calendar.JULY, 6);

        Calendar calendarNow = Calendar.getInstance();
        calendarNow.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH), calendarNow.get(Calendar.DATE),
                0, 0, 0);
        long days = (weddingCalendar.getTimeInMillis() - calendarNow.getTimeInMillis()) / (24 * 60 * 60 * 1000);
        if (days > 0) {
           return String.valueOf(days);
        } else {
            return "0";
        }
    }
}
