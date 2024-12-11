package com.bodhitech.it.lib_base.lib_base;

public class BaseConstants {

    private static final String BASE_TAG = "com.bodhitech.it";

    //region [#] Generic Constants
    // Google Apps - Package Names
    public static final String PCKG_GOOGLE_MAPS = "com.google.android.apps.maps";
    public static final String PCKG_GOOGLE_FEEDBACK = "com.google.android.feedback";

    // Android Apps - Package Names
    public static final String PCKG_ANDROID_VENDING = "com.android.vending";

    // Components Tags
    public static final String TAG_VIEWPAGER_FRAGMENTS = "android:switcher:%1$s:%2$s"; // 1 -> idViewPager, 2 -> fragmentPosition

    // Components Fields Names
    public static final String FIELD_PROGRESS_DIALOG_TV_MESSAGEVIEW = "mMessageView";

    // Google Play Store URI/URL Format Strings
    public static final String GOOGLE_PLAY_STORE_APP_URI = "market://details?id=%1$s";
    public static final String GOOGLE_PLAY_STORE_WEB_URL = "https://play.google.com/store/apps/details?id=%1$s";

    // Design Constants
    public static final float ALPHA_FULL = 1.0f;

    // Base Time Constants
    public static final long MILLIS_QUARTER_SECOND = 250L;
    public static final long MILLIS_HALF_SECOND = 500L;
    public static final long MILLIS_ONE_SECOND = 0x2 * MILLIS_HALF_SECOND;
    public static final long MILLIS_QUARTER_MINUTE = 15 * MILLIS_ONE_SECOND;
    public static final long MILLIS_HALF_MINUTE = 30 * MILLIS_ONE_SECOND;
    public static final long MILLIS_ONE_MINUTE = 0x2 * MILLIS_HALF_MINUTE;
    public static final long MILLIS_HALF_HOUR = 30 * MILLIS_ONE_MINUTE;
    public static final long MILLIS_ONE_HOUR = 0x2 * MILLIS_HALF_HOUR;

    // Google Maps URI
    public static final String GOOGLE_MAP_NAVIGATION_URI = "google.navigation:q=";

    // Decimal Format
    public static final String DECIMAL_FORMAT_DOT_1_DIGIT = "#0.0";
    public static final String DECIMAL_FORMAT_DOT_2_DIGITS = "#0.00";

    // Bytes
    public static final int KBYTE_SIZE = 1024;                          // Bytes in KBytes
    public static final int MBYTE_SIZE = KBYTE_SIZE * KBYTE_SIZE;
    //endregion

    //region [#] Encoding & Keywords
    public static final String[][] HTML_TAGS = new String[][]{new String[]{"<u>", "<b>"},     new String[]{"</u>", "</b>"}};
    //endregion

    //region [#] Regexes & Date Formats
    // Regexes
    public static final String REGEX_REMOVE_COLOR_ALPHA = "([#]{1})+[a-zA-Z0-9]{2}+(.*)";
    public static final String REGEX_SPLIT_INCREMENTAL_FILENAME = "(.*)(_[?:(][0-9]+[?:)]){1}(.[a-zA-Z]{3,})$";
    public static final String REGEX_FILENAME_FROM_PATH = "(.*\\/)([^\\/]*){1}$";
    public static final String REGEX_EXT_FROM_FILENAME = "(.*)[\\.]([^\\.]*)$";         // $1 cattura l'estensione del file. Funziona solo se il nome del file ha l'estensione, altrimenti prende tutto il nome del file.
    public static final String REGEX_REMOVE_HTML_TAGS = "(<b>|<\\/b>|<u>|<\\/u>)";      // Not completed, add others tags when needed
    public static final String REGEX_CHECK_BASE64_ENCODED = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
    public static final String REGEX_REMOVE_SPECIAL_CHARS = "[^a-zA-Z0-9_]";
    public static final String REGEX_CHECK_HAS_BASE_URL = "((http|https)\\:(.*)\\.(it|com|org|local))?.*";
    public static final String REGEX_REMOVE_URL_HTTP = "http[s]?:\\/\\/";
    public static final String REGEX_REMOVE_WEB_HEADER_MIME_BASE64 = "data:[a-zA-Z\\/]*;base64,";
    public static final String REGEX_GET_FILE_EXT = ".*[.?]+(.*.)";                     // $1 cattura l'estensione del file. Funziona solo se il nome del file ha l'estensione, altrimenti prende tutto il nome del file.
    public static final String REGEX_DECIMAL_POINT = "[.]";

    // Date Formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_FORMAT_DB = "yyyy-MM-dd";
    public static final String DATE_FORMAT_FILE = "dd-MM-yyyy";
    public static final String DATE_FORMAT_UNSPACED = "yyyyMMdd";
    public static final String TIME_FORMAT_NO_SECONDS = "HH:mm";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT_NO_SECONDS_REVERSED = "HH:mm dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_MILLIS = "dd/MM/yyyy HH:mm:ss.SSS";
    public static final String DATETIME_FORMAT_NO_SECONDS = "dd/MM/yyyy HH:mm";
    public static final String DATETIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_DB_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATETIME_FORMAT_T = "yyyy-MM-dd'T'hh:mm:ss";
    public static final String DATETIME_FORMAT_T_Z = "yyyy-MM-dd'T'hh:mm:ss'Z'";
    public static final String DATETIME_FORMAT_T_MILLIS = "yyyy-MM-dd'T'hh:mm:ss.SSS";
    public static final String DATETIME_FORMAT_T_MILLIS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATETIME_FORMAT_T_ZZZ = "yyyy-MM-dd'T'HH:mm:ssZZZ";                  // Esempio Formato Data: 2019-09-06T00:00:00+02:00
    public static final String DATETIME_FORMAT_UNSPACED = "yyyyMMddHHmmss";
    public static final String DATETIME_FORMAT_UNSPACED_MILLIS = "yyyyMMddHHmmssSSS";
    public static final String DATETIME_FORMAT_FILE = "dd-MM-yyyy_HH-mm-ss";
    public static final String DATETIME_FORMAT_FILE_MILLIS = "dd-MM-yyyy_HH-mm-ss-SSS";
    //endregion

    //region [#] Service & Job Constants
    // Base IDs
    protected static final Integer BASE_JOB_ID = 0xA989AA0;
    //endregion

    //region [#] Intent Actions & Extras
    // Android System Actions
    public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";

    // Intent Extras
    public static final String BASE_TAG_EXTRA = BASE_TAG + ".extra";
    public static final String EXTRA_JOB_TIMING = BASE_TAG_EXTRA + ".jobTiming";
    //endregion

    //region [#] Notifications Constants
    // Base
    protected static final int BASE_NOTIFICATION_ID = 0x990000;
    private static final String BASE_TAG_TAIL_NOTIFICATION = ".notification";
    private static final String BASE_TAG_TAIL_NOTIFICATION_CHANNEL = ".channel";
    protected static final String BASE_TAG_TAIL_NOTIFICATION_CHANNEL_ID = ".id";
    //endregion

    //region [#] Files Data
    // Content Types
    public static final String CONTENT_TYPE_PLAIN_TEXT = "plain/text";

    // Mime Types
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_IMAGE_JPG = "image/jpg";

    // Extensions
    public static final String EXT_APK = "apk";
    public static final String EXT_PDF = "pdf";
    public static final String EXT_JPG = "jpg";
    public static final String EXT_JPEG = "jpeg";
    public static final String EXT_PNG  = "png";
    public static final String EXT_ZIP = "zip";
    public static final String EXT_RAR = "rar";
    public static final String EXT_DOC = "doc";
    public static final String EXT_BMP = "bmp";
    public static final String EXT_XML = "xml";

    // Path Format Strings
    public static final String PATH_FORMAT = "%1$s/%2$s";
    public static final String PATH_FORMAT_WITH_EXT = "%1$s/%2$s.%3$s";
    public static final String PATH_EXT_FORMAT = "%1$s.%2$s";

    // Filenames Format Strings
    public static final String FILENAME_TEMP_FILE = "tempfile_%1$s_%2$s";               // datetime, random
    //endregion

}