package com.bodhitech.it.lib_base.lib_base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bodhitech.it.lib_base.BuildConfig;
import com.bodhitech.it.lib_base.lib_base.modules.loggers.BaseExceptionManager;
import com.bodhitech.it.lib_base.lib_base.modules.loggers.BaseLogger;
import com.bodhitech.it.lib_base.lib_base.modules.utils.EnvironmentUtils;
import com.bodhitech.it.lib_base.lib_base.modules.utils.PositionUtils;

public abstract class BaseEnvironment {

    private static final String TAG = BaseEnvironment.class.getSimpleName();
    // Constants Values
    protected static final int INT_UNDEFINED = -0x1;

    private static BaseExceptionManager mExceptionManager;
    private static BaseLogger mLogger;

    public BaseEnvironment(BaseExceptionManager exManager){
        mExceptionManager = exManager;
    }

    public BaseEnvironment(BaseExceptionManager exManager, BaseLogger logger){
        mExceptionManager = exManager;
        mLogger = logger;
    }

    /** Static Getter Methods **/
    public static boolean isAppDebugMode(){
        return BuildConfig.DEBUG;
    }

    public static <T extends BaseLogger> T getLogger(){
        return (T) mLogger;
    }

    /** Public Static Methods **//** Manage Exception Methods **/
    public static <T extends Exception> void onExceptionLevelLow(@Nullable Object tag, @NonNull T exception){
        if(hasExceptionManager()){
            mExceptionManager.onException(tag, exception, BaseExceptionManager.EXCEPTION_LEVEL_LOW);
        }
    }

    public static <T extends Exception> void onExceptionLevelNormal(@Nullable Object tag, @NonNull T exception){
        if(hasExceptionManager()){
            mExceptionManager.onException(tag, exception, BaseExceptionManager.EXCEPTION_LEVEL_MEDIUM);
        }
    }

    public static <T extends Exception> void onExceptionLevelHigh(@Nullable Object tag, @NonNull T exception){
        if(hasExceptionManager()){
            mExceptionManager.onException(tag, exception, BaseExceptionManager.EXCEPTION_LEVEL_HIGH);
        }
    }

    public static <T extends Exception> void onNetworkException(@Nullable Object tag, @NonNull T exception){
        if(hasExceptionManager()){
            mExceptionManager.onNetworkException(tag, exception);
        }
    }

    /** Logs Methods **/
    public static void net(Object tag, String msg){
        mLogger.net(tag, msg);
    }

    /** Check Methods **/
    public static boolean hasExceptionManager(){
        return mExceptionManager != null;
    }

    public static boolean hasLogger(){
        return mLogger != null;
    }

    /** Get Device & Application Info Methods **/
    public static String getAppVersionName(Context context) {
        try {
            return EnvironmentUtils.getMyAppVersionName(context);
        } catch (PackageManager.NameNotFoundException nnfE) {
            onExceptionLevelLow(TAG, nnfE);
        }
        return "";
    }

    public static Integer getAppVersionCode(Context context) {
        try {
            return EnvironmentUtils.getMyAppVersionCode(context);
        } catch (PackageManager.NameNotFoundException nnfE) {
            Log.e(TAG, nnfE.getMessage());
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return INT_UNDEFINED;
    }

    public static String getDeviceUniqueID(Context context) {
        return EnvironmentUtils.getMyDeviceUniqueID(context);
    }

    public static String getDeviceName() {
        return EnvironmentUtils.getMyDeviceName();
    }

    public static String getAndroidRelease() {
        return EnvironmentUtils.getMyAndroidRelease();
    }

    /** Protected Static Methods **//** Init Methods **/
    protected static void initPositionUtilsGeocoder(Context context){
        PositionUtils.initGeocoder(context);
    }

}
