package com.emax.blesensors_testing_2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private static final String LOGCAT = DateUtils.class.getSimpleName();

    public static String convertDateFormatTZ(String cDate) throws ParseException {
        Date rDate = getDateTZ(cDate);
        return getDateStringTZ(rDate);
    }

    public static Date getDateTZ(String cDate) throws ParseException {
        SimpleDateFormat cFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date rDate = cFormat.parse(cDate.replaceAll("Z$", "+0000"));
        return rDate;
    }

    public static Integer getMaxDayOfMonth(Integer year, Integer month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        Integer numDays = calendar.getActualMaximum(Calendar.DATE);
        return numDays;
    }

    public static ArrayList<Integer> getYyyyMmDdFromStringTZ(String cDate) throws ParseException {
        ArrayList<Integer> yyyyMmDd = new ArrayList<>();
        SimpleDateFormat cFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        cFormat.setTimeZone(TimeZone.getTimeZone(("Europe/Rome")));
        cDate = cDate.replaceAll("Z$", "+0000");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cFormat.parse(cDate));
        yyyyMmDd.add(calendar.get(Calendar.YEAR));
        yyyyMmDd.add(calendar.get(Calendar.MONTH) + 1);
        yyyyMmDd.add(calendar.get(Calendar.DAY_OF_MONTH));
        yyyyMmDd.add(calendar.get(Calendar.HOUR_OF_DAY));
        yyyyMmDd.add(calendar.get(Calendar.MINUTE));
        yyyyMmDd.add(calendar.get(Calendar.SECOND));
        return yyyyMmDd;
    }

    public static Integer getNumericDayOfWeek(Integer yyyy, Integer mm, Integer dd){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yyyy );
        calendar.set(Calendar.MONTH, mm - 1);
        calendar.set(Calendar.DAY_OF_MONTH, dd);
        Integer day = calendar.get(Calendar.DAY_OF_WEEK);
        return day;
    }

    public static Integer getCurrentYear(){
        Integer year = Calendar.getInstance().get((Calendar.YEAR));
        return year;
    }

    public static Integer getCurrentMonth(){
        Integer month = Calendar.getInstance().get((Calendar.MONTH)) + 1;
        return month;
    }

    private static String getDateStringTZ(Date cDate){
        SimpleDateFormat rFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        rFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        String rDate = rFormat.format(cDate);
        return rDate;
    }

}
