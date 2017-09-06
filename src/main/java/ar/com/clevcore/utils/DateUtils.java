package ar.com.clevcore.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateUtils {

    public static final String PATTERN_DATE = "dd/MM/yyyy";

    private DateUtils() {
        throw new AssertionError();
    }

    public static Date getDate(String value, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(value);
    }

    public static Date getDateNotTolerant(String value, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf.parse(value);
    }

    public static Calendar getCalendar() {
        return new GregorianCalendar();
    }

    public static Calendar getCalendarNotTolerant() {
        Calendar calendar = new GregorianCalendar();
        calendar.setLenient(false);
        return calendar;
    }

    public static Date addYear(Date date, int year) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.YEAR, year);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date addMonth(Date date, int month) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.MONTH, month - 1);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date addDay(Date date, int day) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDate(int year, int month, int day) {
        Calendar calendar = getCalendarNotTolerant();
        calendar.set(year, month - 1, day, 0, 0, 0);
        return new Date(calendar.getTimeInMillis());
    }

    public static Calendar getCalendar(int year, int month, int day) {
        Calendar calendar = getCalendarNotTolerant();
        calendar.set(year, month - 1, day, 0, 0, 0);
        return calendar;
    }

    public static int getYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return Integer.parseInt(sdf.format(date));
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static Calendar setYear(Calendar calendar, int value) {
        calendar.set(Calendar.YEAR, value);
        return calendar;
    }

    public static int getMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return Integer.parseInt(sdf.format(date));
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static Calendar setMonth(Calendar calendar, int value) {
        calendar.set(Calendar.MONTH, value - 1);
        return calendar;
    }

    public static int getDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return Integer.parseInt(sdf.format(date));
    }

    public static int getDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar setDay(Calendar calendar, int value) {
        calendar.set(Calendar.DAY_OF_MONTH, value);
        return calendar;
    }

    public static int getActualMaximumDay(Calendar calendar) {
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public static Long getAge(Date date) {
        Date sysdate = new Date();
        return (long) Math.floor((sysdate.getTime() - date.getTime()) / 31557600000L);
    }

    public static String getDateFormat(Date date, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getDateFormat(Date date, String pattern, TimeZone timeZone) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }

}
