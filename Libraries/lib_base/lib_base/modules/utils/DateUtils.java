package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.SystemClock;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.lib_base.modules.networking.clients.SntpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public interface IDatePickerCallback {
        void onDateSelected(int requestCode, String date, EditText editText);
    }

    public interface ITimePickerCallback {
        void onTimeSelected(int requestCode, String time, EditText editText);
    }

    private static final String TAG = DateUtils.class.getSimpleName();
    // Date Format Digits
    private static final String DATE_FORMAT_Z_LAST_DIGIT = "Z";
    // Date Time Zones
    private static final String TIMEZONE_GMT = "GMT";
    // Date Values
    public static final String VALUE_EMPTY_DATE = "00-00-0000";
    public static final String VALUE_EMPTY_DATE_DB = "0000-00-00";

    /** Public Methods **/
    public static String convertDateFormat(String data, String fmtIn, String fmtOut) {
        try {
            return getStringFromDate(convertStringToDate(data, fmtIn), fmtOut);
        } catch(ParseException pE){
            BaseEnvironment.onExceptionLevelLow(TAG, pE);
        }
        return null;
    }

    public static Date convertDateFormat(Date date, String format){
        return getDateFromString(getStringFromDate(date, format), format);
    }

    public static String getStringFromDate(Date date, String formatOut) {
        return new SimpleDateFormat(formatOut).format(date);
    }

    public static String getStringFromDate(Date date, String formatOut, TimeZone tz) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatOut);
        formatter.setTimeZone(tz);
        return formatter.format(date);
    }

    public static Date getDateFromString(String date, String formatIn){
        try{
            return convertStringToDate(date, formatIn);
        } catch(ParseException pE){
            BaseEnvironment.onExceptionLevelLow(TAG, pE);
        }
        return null;
    }

    public static Date getDateFromStringUnmanaged(String date, String formatIn) throws ParseException {
        return convertStringToDate(date, formatIn);
    }

    /**
     * Add 'num' days to the input date.
     *
     * @param date  The date to which add days.
     * @param num Number of days to add.
     * @return  Input date plus 'n' days.
     */
    public static Date addDaysToDate(@NonNull Date date, int num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

    /**
     * Add 'num' months to the input date.
     *
     * @param date  The date to which add months.
     * @param num   Number of months to add.
     * @return  Input date plus 'n' months.
     */
    public static Date addMonthsToDate(@NonNull Date date, int num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        return calendar.getTime();
    }

    public static int getDiffDays(Date dateOne, Date dateTwo){
        Long timeOne = dateOne.getTime();
        Long timeTwo = dateTwo.getTime();
        long diff = timeOne - timeTwo ;
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /** DataPicker Methods **/
    public static void showDatePicker(@NonNull Context context, @Nullable final EditText dateView, Date currDate){
        showDatePicker(context, dateView, currDate, null);
    }

    public static void showDatePicker(@NonNull Context context, @Nullable final EditText dateView, @Nullable Date currDate, @Nullable final IDatePickerCallback callback){
        showDatePicker(context, dateView, currDate, callback, 0x0);
    }

    public static void showDatePicker(@NonNull Context context, @Nullable final EditText dateView, @Nullable Date currDate, @Nullable final IDatePickerCallback callback, int requestCode){
        final Calendar now = Calendar.getInstance();
        if(currDate != null && checkDate(currDate)){
            now.setTime(currDate);
        }
        DatePickerDialog dialog = new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                (datePicker, year, month, dayOfMonth) -> {
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.YEAR, year);
                    date.set(Calendar.MONTH, month);
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    if(callback != null){
                        callback.onDateSelected(requestCode, getStringFromDate(date.getTime(), BaseConstants.DATE_FORMAT), dateView);
                    } else if(dateView != null){
                        dateView.setText(getStringFromDate(date.getTime(), BaseConstants.DATE_FORMAT));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    /** TimePicker Methods **/
    public static void showTimePicker(@NonNull Context context, @Nullable final EditText timeView, @Nullable Date currDate){
        showTimePicker(context, timeView, currDate, null);
    }

    public static void showTimePicker(@NonNull Context context, @Nullable final EditText timeView, @Nullable Date currDate, @Nullable final ITimePickerCallback callback){
        showTimePicker(context, timeView, currDate, callback, 0x0);
    }

    public static void showTimePicker(@NonNull Context context, @Nullable final EditText timeView, @Nullable Date currDate, @Nullable final ITimePickerCallback callback, int requestCode){
        final Calendar now = Calendar.getInstance();
        if(currDate != null && checkDate(currDate)){
            now.setTime(currDate);
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    Calendar time = Calendar.getInstance();
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    time.set(Calendar.MINUTE, minute);
                    if(callback != null){
                        callback.onTimeSelected(requestCode, getStringFromDate(time.getTime(), BaseConstants.TIME_FORMAT_NO_SECONDS), timeView);
                    } else if(timeView != null){
                        timeView.setText(getStringFromDate(time.getTime(), BaseConstants.TIME_FORMAT_NO_SECONDS));
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    /** Check Methods **/
    public static boolean checkDateFormat(String date, String dateFormat){
        try{
            return checkDate(convertStringToDate(date, dateFormat));
        } catch(ParseException pE){
            //BaseEnvironment.onExceptionLevelLow(TAG, pE);
        }
        return false;
    }

    public static boolean checkDate(Date date){
        try{
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.setTime(date);
            cal.getTime();
            return true;
        } catch(Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return false;
    }

    /** Current Date Methods **/
    public static String getCurrentDateString(String dateFormat){
        return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
    }

    public static Date getCurrentDateNoTime(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0x0);
        today.set(Calendar.MINUTE, 0x0);
        today.set(Calendar.SECOND, 0x0);
        today.set(Calendar.MILLISECOND, 0x0);
        return today.getTime();
    }

    public static int getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Date getCurrentDate(String dateFormat){
        return getDateFromString(getCurrentDateString(dateFormat), dateFormat);
    }

    public static Long getCurrentUnixTime(String dateFormat) {
        try{
            return getCurrentDate(dateFormat).getTime() / BaseConstants.MILLIS_ONE_SECOND;
        } catch(Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }

    public static String getCurrentDateFromSNTP(String format){
        return getStringFromDate(getCurrentDateFromSNTP(), format);
    }

    public static Date getCurrentDateFromSNTP() {
        try {
            SntpClient client = new SntpClient();
            if (client.requestTime(SntpClient.NTP_SERVER, SntpClient.NTP_TIMEOUT)) {
                long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
                return new Date(now);
            }
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return new Date();
    }

    public static int compareWithCurrentDate(Date date){
        Date current = org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        return date.compareTo(current);
    }

    /** Private Methods **/
    private static Date convertStringToDate(String date, String formatIn) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatIn);
        if(date.endsWith(DATE_FORMAT_Z_LAST_DIGIT)){
            formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_GMT));
        }
        return formatter.parse(date);
    }

}