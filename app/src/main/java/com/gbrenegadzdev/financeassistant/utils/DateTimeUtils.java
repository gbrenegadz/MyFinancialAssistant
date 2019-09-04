package com.gbrenegadzdev.financeassistant.utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public Date getCurrentDatetime() {
        return new Date();
    }

    public String getStringMonth(Date date) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // don't forget this if date is arbitrary
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 being Sunday
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        int month = cal.get(Calendar.MONTH); // 0 being January
        int year = cal.get(Calendar.YEAR);

        return new DateFormatSymbols().getMonths()[month-1];
    }

    public int getIntMonth(Date date) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // don't forget this if date is arbitrary
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 being Sunday
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        int month = cal.get(Calendar.MONTH); // 0 being January
        int year = cal.get(Calendar.YEAR);

        return month;
    }


    public int getIntYear(Date date) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // don't forget this if date is arbitrary
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 being Sunday
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        int month = cal.get(Calendar.MONTH); // 0 being January
        int year = cal.get(Calendar.YEAR);

        return year;
    }

    public int getIntDayOfMonth(Date date) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // don't forget this if date is arbitrary
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 being Sunday
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        int month = cal.get(Calendar.MONTH); // 0 being January
        int year = cal.get(Calendar.YEAR);

        return dayOfMonth;
    }
}
