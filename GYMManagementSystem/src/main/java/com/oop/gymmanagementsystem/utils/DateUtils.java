package com.oop.gymmanagementsystem.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    public static String today() {
        return DATE_FORMAT.format(new Date());
    }

    public static String currentMonth() {
        return MONTH_FORMAT.format(new Date());
    }

    public static String formatDisplay(String dateStr) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            return DISPLAY_FORMAT.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    public static String getMonthName(String monthStr) {
        try {
            String[] parts = monthStr.split("-");
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int monthIndex = Integer.parseInt(parts[1]) - 1;
            return months[monthIndex] + " " + parts[0];
        } catch (Exception e) {
            return monthStr;
        }
    }

    public static boolean isExpired(String dateStr, int validMonths) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, validMonths);
            return cal.getTime().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
