package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.DatePicker;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.ArrayUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class DataUtils {

    private static final String TAG = DataUtils.class.getSimpleName();
    // String Values
    private static final String STR_NULL = "null";
    // Constants values
    public static final int INT_UNDEFINED = -0x1;
    // Number Format Strings
    private static final String FSTR_TWO_FIELDS_DOT = "%1$s.%2$s";
    // Signs
    private static final String SIGN_EURO = "\u20AC";
    // Times Millis
    private static final long MILLIS_IN_DAY = BaseConstants.MILLIS_ONE_HOUR * 24;
    private static final long MILLIS_IN_WEEK = MILLIS_IN_DAY * 0x7;
    private static final long MILLIS_IN_MONTH = MILLIS_IN_DAY * 30;
    private static final long MILLIS_IN_YEAR = MILLIS_IN_MONTH * 12;
    // Times Minutes
    private static final int MINUTES_IN_HOUR = 60;
    private static final int MINUTES_IN_DAY = MINUTES_IN_HOUR * 24;
    private static final int MINUTES_IN_WEEK = MINUTES_IN_DAY * 7;
    // DateTime Format Strings
    private static final String FSTR_YEARS = "%1$s anni";
    private static final String FSTR_MONTHS = "%1$s mesi";
    private static final String FSTR_WEEKS = "%1$s settimane";
    private static final String FSTR_DAYS = "%1$s giorni";
    private static final String FSTR_HOURS = "%1$s ore";
    private static final String FSTR_MINUTES = "%1$s minuti";
    // DateTime Labels
    private static final String LABEL_ONE_YEAR = "1 anno";
    private static final String LABEL_ONE_MONTH = "1 mese";
    private static final String LABEL_ONE_WEEK = "1 settimana";
    private static final String LABEL_ONE_DAY = "1 giorno";
    private static final String LABEL_ONE_DAY_ONE_HOUR = "1 giorno 1 ora";
    private static final String LABEL_ONE_HOUR = "1 ora";
    private static final String LABEL_ONE_MINUTE = "1 minuto";

    //region [#] Public Static Methods
    //region [#] String & CharSequence Methods
    public static CharSequence ifEmpty(CharSequence str, CharSequence def) {
        return TextUtils.isEmpty(str) ? def : str;
    }

    public static String ifEmpty(String str, String def) {
        return TextUtils.isEmpty(str) ? def : str;
    }

    public static String rightPadString(String input, String pad, int lenght){
        if(input == null){
            input = "";
        }
        StringBuilder sb = new StringBuilder(input);
        if(sb.length() < lenght){
            for(int i = 0x0; i < (lenght - input.length() - pad.length()); i+= pad.length()){
                sb.append(pad);
            }
            if(lenght % pad.length() != 0x0){
                sb.append(pad.substring(0x0, (lenght % pad.length())));
            }
        }
        return sb.toString();
    }

    public static String leftPadString(String input, String pad, int lenght){
        if(input == null){
            input = "";
        }
        StringBuilder sb = new StringBuilder();
        if(sb.length() < lenght){
            for(int i = 0x0; i < (lenght - input.length()); i+= pad.length()){
                sb.append(pad);
            }
            if(lenght % pad.length() != 0x0){
                sb.append(pad.substring(0x0, (lenght % pad.length())));
            }
            sb.append(input);
        }
        return sb.toString();
    }
    //endregion

    //region [#] Regexes Methods
    public static String applyRegex(String str, String regex, String replace){
        return !TextUtils.isEmpty(str) ?  Pattern.compile(regex).matcher(str).replaceAll(replace) : "";
    }

    public static String applyRegex(String str, String regex, String replace, int flags){
        return !TextUtils.isEmpty(str) ?  Pattern.compile(regex, flags).matcher(str).replaceAll(replace) : "";
    }

    public static String applyRegexMultiline(String str, String regex, String replace){
        return !TextUtils.isEmpty(str) ?  Pattern.compile(regex, Pattern.MULTILINE).matcher(str).replaceAll(replace) : "";
    }

    public static String applyRegexMultilineReplaceNulls(String str, String regex, String replace){
        String res = applyRegexMultiline(str, regex, replace);
        return res.contains(STR_NULL) ? res.replaceAll(STR_NULL, "") : res;
    }
    //endregion

    //region [#] Validators Methods
    public static boolean checkValidEmail(@NonNull String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    //endregion

    //region [#] Resources Methods
    public static String[] getStringsFromResources(Context context, int[] resIds){
        String[] strings = new String[resIds.length];
        for(int i = 0x0; i < resIds.length; i++){
            strings[i] = resIds[i] != INT_UNDEFINED ? context.getString(resIds[i]) : "";
        }
        return strings;
    }
    //endregion

    //region [#] OutputStream Methods
    public static String getStringFromInputStream(InputStream in){
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))){
            String line;
            while((line = br.readLine()) != null){
                builder.append(line).append('\n');
            }
        } catch (IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return builder.toString();
    }
    //endregion

    //region [#] Formatters Methods
    public static String doubleFormatterDotTwoDigitsEuroUS(double dNumber){
        return doubleFormatterDot(dNumber, BaseConstants.DECIMAL_FORMAT_DOT_2_DIGITS, Locale.US)  + " " + SIGN_EURO;
    }

    public static String doubleFormatterDotTwoDigitsUS(double dNumber){
        return doubleFormatterDot(dNumber, BaseConstants.DECIMAL_FORMAT_DOT_2_DIGITS, Locale.US);
    }

    public static String doubleFormatterDotNoLocale(double num, String decimalFormat){
        return doubleFormatterDot(num, decimalFormat, null);
    }

    public static String doubleFormatterDot(double num, String decimalFormat, @Nullable Locale locale){
        String ret = "0";
        if(num != 0x0){
            DecimalFormat df = new DecimalFormat(decimalFormat);
            String str = df.format(num);
            if(str.contains(",")){
                str = str.replace(',', '.');
            }
            if(locale != null){
                String[] splitted = str.split(BaseConstants.REGEX_DECIMAL_POINT);
                ret = String.format(FSTR_TWO_FIELDS_DOT, NumberFormat.getNumberInstance(locale).format(DataUtils.checkIsInteger(splitted[0x0]) ?
                        DataUtils.getAsInteger(splitted[0x0]) : DataUtils.getAsLong(splitted[0x0])), splitted[0x1]);
            } else {
                ret = str;
            }
        }
        return ret;
    }

    public static String doubleFormatter(double num){
        return new DecimalFormat().format(num);
    }

    public static String doubleFormatter(double num, String decimalFormat){
        return new DecimalFormat(decimalFormat).format(num);
    }
    //endregion

    //region [#] Int, Double & Float Getter, Checker & Converter Methods
    @Nullable
    public static Integer getAttributeDimensionPixelSize(@NonNull Context context, @AttrRes int resId){
        TypedValue tv = new TypedValue();
        if(context.getTheme().resolveAttribute(resId, tv, true)){
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return null;
    }

    public static int getNumDecimalDigits(double num){
        return getNumDecimalDigits(String.valueOf(num));
    }

    public static int getNumDecimalDigits(String num){
        int ret = 0x0;
        if(num.contains(".")){
            String[] splitted = num.split("[.]");
            if(splitted.length > 0x1) {
                ret = splitted[0x1].length();
            }
        }
        return ret;
    }

    public static int getRandomInteger(Integer mul){
        return (int) Math.round(Math.random() * mul);
    }

    public static int getRandomInteger(){
        return getRandomInteger(100);
    }

    public static int[] getIntRange(int from, int to){
        int[] range = new int[Math.abs(to - from)];
        int mul = (from < to) ? 0x1 : -0x1;
        for(int i=0; i <= (Math.abs((from - to))); i++){
            range[i] = from + (i * mul);
        }
        return range;
    }

    public static boolean checkIsIntegerNoLog(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException ignored){

        }
        return false;
    }

    public static boolean checkIsInteger(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return false;
    }

    public static boolean checkIsDouble(String str){
        try{
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return false;
    }

    public static boolean checkIsLong(String str){
        try{
            Long.parseLong(str);
            return true;
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return false;
    }

    public static Integer getAsInteger(String str){
        try{
            return Integer.parseInt(str);
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return null;
    }

    public static Integer getAsInteger(Double dbl){
        try{
            return ((Long) Math.round(dbl)).intValue();
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return null;
    }

    public static Double getAsDouble(String str){
        try{
            return Double.parseDouble(str);
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return null;
    }

    public static Long getAsLong(String str){
        try{
            return Long.parseLong(str);
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return null;
    }

    public static Float getAsFloat(String str){
        try{
            return Float.parseFloat(str);
        } catch(NumberFormatException nfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nfE);
        }
        return null;
    }

    public static long toUnsignedLong(byte bValue){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                Byte.toUnsignedLong(bValue) :
                (long) bValue & 0xFF;
    }

    public static String getColorAsString(Context context, int idColor){
        return context.getString(idColor).replaceAll(BaseConstants.REGEX_REMOVE_COLOR_ALPHA, "$1$2");
    }
    //endregion

    //region [#] Bytes Methods
    public static double getKiloBytes(long bytes){
        return (double) bytes / BaseConstants.KBYTE_SIZE;
    }

    public static double getMegaBytes(long bytes){
        return (double) bytes / BaseConstants.MBYTE_SIZE;
    }
    //endregion

    //region [#] Arrays, Lists & Matrices Methods
    public static int[] getFilledIntArray(int length, int fill){
        int[] enabled = new int[length];
        Arrays.fill(enabled, fill);
        return enabled;
    }

    public static int[][] copyMatrix(int[][] old, int rows, int columns){
        int[][] matrix = new int[rows != -0x1 ? rows : old.length][];
        for(int i=0x0; i < old.length && (rows == -0x1 || i < rows); i++){
            matrix[i] = columns != -0x1 ? Arrays.copyOf(old[i], columns) : Arrays.copyOf(old[i], old[i].length);
        }
        return matrix;
    }

    public static <T extends Object> T[][] copyMatrix(T[][] old, int rows, int columns){
        T[][] matrix = (T[][]) new Object[rows != -0x1 ? rows : old.length][];
        for(int i=0x0; i < old.length && (rows == -0x1 || i < rows); i++){
            matrix[i] = columns != -0x1 ? Arrays.copyOf(old[i], columns) : Arrays.copyOf(old[i], old[i].length);
        }
        return matrix;
    }

    public static <T> List<T> doListOrdering(boolean asc, List<T> list, String methodName){
        List<T> ret = null;
        try {
            Method method = list.get(0x0).getClass().getMethod(methodName);
            ret = doOrdering(asc, list, method);
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return ret;
    }

    public static int[] matrixToArray(int[][] matrix){
        /* [+] Square matrix of order n    ->  A matrix with n rows and n columns, same number of rows and columns.
         * [+] Matrix rows & columns number annotations:
         *      matrix[rows][columns]   matrix (rows x columns)     matrix rows, columns    rows by columns matrix
         */
        int numRows = matrix.length;
        int[] arr = new int[]{};
        for(int i = 0x0; i < numRows; i++){
            int numColumns = matrix[i].length;
            int[] row = new int[numColumns];
            for(int j = 0x0; j < numColumns; j++){
                row[j] = matrix[i][j];
            }
            arr = ArrayUtils.addAll(arr, row);
        }
        return arr;
    }

    public static int[][] arrayToMatrix(int[] array, int numColumns){
        int numRows = array.length / numColumns;
        int[][] matrix = new int[numRows][numColumns];
        int nElemens = array.length;
        for(int i = 0x0; i < nElemens; i++){
            matrix[i / numColumns][i % numColumns] = array[i];
        }
        return matrix;
    }

    public static int[] resourcesIDsToColors(Context context, int[] resIDs){
        int[] colors = new int[resIDs.length];
        for(int i = 0x0; i < resIDs.length; i++){
            colors[i] = ActivityCompat.getColor(context, resIDs[i]);
        }
        return colors;
    }

    /**
     * Convert an array of primitive int (int[]) to an array of int objects (Integer[]).
     *
     * @param src   The source array
     * @return  The Integer array
     */
    public static Integer[] toIntegerArray(int[] src){
        return ArrayUtils.toObject(src);
        /*  Altri metodi fighi per fare la conversione da int[] a Integer[], ma richiedono come minimo API 24:

            Arrays.stream(arr).boxed().toArray(Integer[]::new);
            IntStream.of(arr).boxed().toArray(Integer[]::new);
            Arrays.setAll(dest, i -> src[i]);
        */
    }

    /**
     * Convert an array of primitive int (int[]) to an array of String objects (String[]).
     *
     * @param src   The source array
     * @return  The String array
     */
    public static String[] toStringArray(int[] src){
        return Arrays.toString(src).split("[\\[\\]]")[1].split(", ");
    }
    //endregion

    //region [#] Intents Methods
    @NonNull
    public static List<Intent> addIntentToList(@NonNull Context context, @NonNull List<Intent> list, @NonNull Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0x0);
        for (ResolveInfo resolveInfo : resInfo) {
            list.add(new Intent(intent).setPackage(resolveInfo.activityInfo.packageName));
        }
        return list;
    }
    //endregion

    //region [#] Models Methods
    public static Map<String, String> getModelAsMap(Object model) {
        Map<String, String> fieldsMap = new HashMap<>();
        if (model == null) {
            return fieldsMap;
        }
        Class<?> objClass = model.getClass().getSuperclass();
        for (Field field : objClass.getDeclaredFields()) {
            SerializedName annotation = field.getAnnotation(SerializedName.class);
            if (annotation != null) {
                try {
                    String value = field.get(model) == null ? "" : field.get(model).toString();
                    fieldsMap.put(annotation.value(), value);
                } catch (IllegalAccessException iaE) {
                    BaseEnvironment.onExceptionLevelLow(TAG, iaE);
                }
            }
        }
        return fieldsMap;
    }
    //endregion

    //region [#] Fields Methods
    @Nullable
    public static Object getValueOfField(@NonNull Object object, @NonNull String name){
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for(Field field : fields){
                if(field.getName().equals(name)){
                    field.setAccessible(true);
                    return field.get(object);
                }
            }
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return null;
    }

    @NonNull
    public static List<Object> getValuesOfFields(@NonNull DatePicker object, @NonNull List<String> names){
        List<Object> instances = new ArrayList<>();
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for(Field field : fields){
                if(names.contains(field.getName())){
                    field.setAccessible(true);
                    instances.add(field.get(object));
                }
            }
        } catch (Exception e) {
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
        return instances;
    }
    //endregion

    //region [#] XML Parsing Methods
    public static <T extends Object> List<HashMap<String, T>> parseStringXML(String xml){
        List<HashMap<String, T>> ret = new ArrayList<>();
        if(xml != null && !TextUtils.isEmpty(xml)){
            try{
                XmlPullParserFactory xppFactory = XmlPullParserFactory.newInstance();
                xppFactory.setNamespaceAware(true);
                XmlPullParser xpp = xppFactory.newPullParser();
                xpp.setInput(new StringReader(xml));
                ret = parseTagsXML(xpp);
            } catch (XmlPullParserException xppE){
                BaseEnvironment.onExceptionLevelLow(TAG, xppE);
            } catch (Exception e){
                BaseEnvironment.onExceptionLevelLow(TAG, e);
            }
        }
        return ret;
    }
    //endregion

    //region [#] Networking URL Formatters Methods
    public static String formatApiUrl(String... relativePaths){
        return relativePaths.length > 0x1 ?
                (relativePaths[0x0].charAt(relativePaths[0x0].length() - 0x1) != '/' && relativePaths[0x1].charAt(0x0) != '/' ?
                        String.format(BaseConstants.PATH_FORMAT, relativePaths[0x0], formatApiUrl(ArrayUtils.subarray(relativePaths, 0x1, relativePaths.length))) :
                        (relativePaths[0x0].charAt(relativePaths[0x0].length() - 0x1) == '/' && relativePaths[0x1].charAt(0x0) == '/' ?
                                relativePaths[0x0].substring(0x0, relativePaths[0x0].length() - 0x1) :
                                relativePaths[0x0]) + formatApiUrl(ArrayUtils.subarray(relativePaths, 0x1, relativePaths.length)))
                : relativePaths[0x0];
    }
    //endregion

    //region [#] Time to Text
    public static String getYMWDTimeStringFromMillis(long millis){
        int years = (int)(millis / MILLIS_IN_YEAR);
        int months = (int)((millis % MILLIS_IN_YEAR) / MILLIS_IN_MONTH);
        int weeks = (int)(((millis % MILLIS_IN_MONTH)) / MILLIS_IN_WEEK);
        int days = (int)((millis % MILLIS_IN_WEEK) / MILLIS_IN_DAY);
        String ret = "";
        if(years > 0x1){
            ret += String.format(FSTR_YEARS, years) + " ";
        } else if(years == 0x1){
            ret += LABEL_ONE_YEAR + " ";
        }
        if(months > 0x1){
            ret += String.format(FSTR_MONTHS, months) + " ";
        } else if(years == 0x1){
            ret += LABEL_ONE_MONTH + " ";
        }
        if(weeks > 0x1){
            ret += String.format(FSTR_WEEKS, weeks) + " ";
        } else if(weeks == 0x1){
            ret += LABEL_ONE_WEEK + " ";
        }
        if(days > 0x1){
            ret += String.format(FSTR_DAYS, days) + " ";
        } else if(days == 0x1){
            ret += LABEL_ONE_DAY + " ";
        }
        return ret.length() > 0x0 ? ret.substring(0x0, ret.length() - 0x1) : "";
    }

    @SuppressLint("DefaultLocale")
    public static String getTextFromMinute(int totMinutes) {
        int weeks = totMinutes / MINUTES_IN_WEEK;
        int days = (totMinutes % MINUTES_IN_WEEK) / MINUTES_IN_DAY;
        int hours = (totMinutes % MINUTES_IN_DAY) / MINUTES_IN_HOUR;
        int minutes = totMinutes % MINUTES_IN_HOUR;
        if (weeks > 0x0) {
            if(days > 0x0) {
                if (days > 0x1) {
                    return String.format(FSTR_WEEKS + " " + FSTR_DAYS.replace('1', '2'), weeks, days);
                } else {
                    return String.format(FSTR_WEEKS + " " + LABEL_ONE_DAY, weeks);
                }
            } else {
                if (weeks > 0x1) {
                    return String.format(FSTR_WEEKS, weeks);
                } else {
                    return LABEL_ONE_WEEK;
                }
            }
        } else if (days > 0x0) {
            if(hours > 0x0){
                if (days > 0x1 && hours > 0x1) {
                    return String.format(FSTR_DAYS + " " + FSTR_HOURS.replace('1', '2'), days, hours);
                } else {
                    if(days > 0x1){
                        return String.format(FSTR_DAYS + " " + LABEL_ONE_HOUR, days);
                    } else {
                        if(hours > 0x1){
                            return String.format(LABEL_ONE_DAY + " " + FSTR_HOURS, hours);
                        } else {
                            return LABEL_ONE_DAY_ONE_HOUR;
                        }
                    }
                }
            } else {
                if (days > 0x1) {
                    return String.format(FSTR_DAYS, days);
                } else {
                    return LABEL_ONE_DAY;
                }
            }
        } else if (hours > 0x0) {
            if(minutes > 0x0){
                if (minutes > 0x1) {
                    return String.format(FSTR_HOURS + " " + FSTR_MINUTES.replace('1', '2'), hours, minutes);
                } else {
                    return String.format(FSTR_HOURS + " " + LABEL_ONE_MINUTE, hours);
                }
            } else {
                if (hours > 0x1) {
                    return String.format(FSTR_HOURS, hours);
                } else {
                    return LABEL_ONE_HOUR;
                }
            }
        } else {
            if (minutes > 0x1) {
                return String.format(FSTR_MINUTES, minutes);
            } else {
                return LABEL_ONE_MINUTE;
            }
        }
    }
    //endregion
    //endregion

    //region [#] Private Static Methods
    //region [#] Generic Object List Methods
    private static <T> List<T> doOrdering(boolean asc, List<T> list, Method method) throws InvocationTargetException, IllegalAccessException {
        T t;
        int type;
        int listSize = list.size();
        List<T> ordered = new ArrayList<>();
        for(int i = 0; i < listSize; i++){
            t = findMinMax(asc, list, method);
            ordered.add(t);
            list.remove(t);
        }
        return ordered;
    }

    private static <T> T findMinMax(boolean asc, List<T> list, Method method) throws InvocationTargetException, IllegalAccessException {
        T ret = null;
        Object minmax = null;
        for(T element : list){
            Object val = method.invoke(element);
            if(minmax == null || ((asc && checkIfBigger(minmax, val)) || (!asc && checkIfBigger(val, minmax)))){
                minmax = val;
                ret = element;
            }
        }
        return ret;
    }
    //endregion

    //region [#] Comparison Methods
    private static boolean checkIfBigger(Object obj1, Object obj2){
        if(obj1 instanceof Integer){
            return checkIfBigger((Integer) obj1, (Integer) obj2);
        } else if(obj1 instanceof Long){
            return checkIfBigger((Long) obj1, (Long) obj2);
        } else if(obj1 instanceof Double){
            return checkIfBigger((Double) obj1, (Double) obj2);
        }
        return false;
    }

    private static boolean checkIfBigger(Integer obj1, Integer obj2){
        return obj1 > obj2;
    }

    private static boolean checkIfBigger(Long obj1, Long obj2){
        return obj1 > obj2;
    }

    private static boolean checkIfBigger(Double obj1, Double obj2){
        return obj1 > obj2;
    }

    private static boolean checkIfSmaller(Object obj1, Object obj2){
        if(obj1 instanceof Integer){
            return checkIfSmaller((Integer) obj1, (Integer) obj2);
        } else if(obj1 instanceof Long){
            return checkIfSmaller((Long) obj1, (Long) obj2);
        } else if(obj1 instanceof Double){
            return checkIfSmaller((Double) obj1, (Double) obj2);
        }
        return false;
    }

    private static boolean checkIfSmaller(Integer obj1, Integer obj2){
        return obj1 < obj2;
    }

    private static boolean checkIfSmaller(Long obj1, Long obj2){
        return obj1 < obj2;
    }

    private static boolean checkIfSmaller(Double obj1, Double obj2){
        return obj1 < obj2;
    }
    //endregion

    //region [#] XML Parsing Methods
    private static <T extends Object> T parseTagsXML(XmlPullParser xpp) {
        int index = 0x0;
        List<HashMap<String, T>> tree = new ArrayList<HashMap<String, T>>(){{add(new HashMap<>());}};
        try{
            List<String> tags = new ArrayList<>();
            int event = 0x0; int lastEvent;
            while(event != XmlPullParser.END_DOCUMENT){
                lastEvent = xpp.getEventType();
                if(lastEvent == XmlPullParser.END_TAG && tags.contains(xpp.getName())){
                    tags.remove(xpp.getName());
                    if(tags.size() == 0x0){
                        return (T) new HashMap<String, T>(){{put(xpp.getName(), null);}};
                    }
                }
                event = xpp.next();
                switch (event){
                    case XmlPullParser.START_TAG:
                        tags.add(xpp.getName());
                        if(tags.size() >= 0x2 && containsStringKeyInMapsTree(tree.get(index), tags.get(tags.size() - 0x2))){
                            tree.set(index, putMapElementInTreeMaps(tags.get(tags.size() - 0x2), tree.get(index), tags.get(tags.size() - 0x1), parseTagsXML(xpp)));
                        } else {
                            tree.get(index).put(tags.get(tags.size() - 0x1), parseTagsXML(xpp));
                        }
                        break;
                    case XmlPullParser.TEXT:
                        return (T) xpp.getText();
                    case XmlPullParser.END_TAG:
                        if(tags.size() > 0x0 && tags.contains(xpp.getName())) {
                            tags.remove(xpp.getName());
                            if(tags.size() == 0x0){
                                if(xpp.getDepth() == 0x1) {
                                    index++;
                                    tree.add(new HashMap<>());
                                    break;
                                } else {
                                    return (T) tree.get(index);
                                }
                            }
                        }
                        if(lastEvent == XmlPullParser.START_TAG){
                            return null;
                        }
                        break;
                }
            }
            if(tree.size() >= index && (tree.get(index) == null || tree.get(index).isEmpty())) {
                tree.remove(index);
            }
        } catch(IOException ioE){
            BaseEnvironment.onExceptionLevelLow(TAG, ioE);
        } catch(XmlPullParserException xppE){
            BaseEnvironment.onExceptionLevelLow(TAG, xppE);
        }
        return (T) tree;
    }
    //endregion

    //region [#] Tree HashMap Methods
    private static <T extends Object> boolean containsStringKeyInMapsTree(HashMap<String, T> tree, String key) {
        if(tree != null){
            if(tree.containsKey(key)){
                return true;
            } else if(tree.size() > 0x0){
                for(String k : tree.keySet()){
                    if(k != null && !TextUtils.isEmpty(k.trim()) && tree.get(k) != null && tree.get(k) instanceof HashMap && containsStringKeyInMapsTree((HashMap<String, T>) tree.get(k), key)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static <T extends Object> HashMap<String, T> putMapElementInTreeMaps(String parentKey, HashMap<String, T> tree, String elementKey, T element){
        if(tree != null){
            if(tree.containsKey(parentKey) && tree.get(parentKey) != null && tree.get(parentKey) instanceof HashMap){
                ((HashMap<String, T>) tree.get(parentKey)).put(elementKey, element);
            } else if(tree.size() > 0x0){
                for(String key : tree.keySet()){
                    if(key != null && !TextUtils.isEmpty(key.trim()) && tree.get(key) != null && tree.get(key) instanceof HashMap){
                        tree.put(key, (T) putMapElementInTreeMaps(parentKey, (HashMap<String, T>) tree.get(key), elementKey, element));
                    }
                }
            }
        }
        return tree;
    }
    //endregion
    //endregion

}
