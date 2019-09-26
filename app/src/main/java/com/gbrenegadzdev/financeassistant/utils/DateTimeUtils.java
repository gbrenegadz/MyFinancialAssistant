package com.gbrenegadzdev.financeassistant.utils;

import com.gbrenegadzdev.financeassistant.models.realm.CategorySetup;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

    public static final String[] MONTHS = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public Date getCurrentDatetime() {
        return new Date();
    }

    public String getStringMonth(Date date) {
        Date today = date;
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
        Date today = date;
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
        Date today = date;
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
        Date today = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // don't forget this if date is arbitrary
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 being Sunday
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        int month = cal.get(Calendar.MONTH); // 0 being January
        int year = cal.get(Calendar.YEAR);

        return dayOfMonth;
    }

    public Date getDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute, second).getTime();
    }

    public int getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);

        return cal.getActualMaximum(Calendar.DATE);
    }
}
