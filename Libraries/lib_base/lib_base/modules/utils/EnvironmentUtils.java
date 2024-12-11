package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.jaredrummler.android.device.DeviceName;
import com.bodhitech.it.lib_base.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class EnvironmentUtils {

    public static final String TAG = EnvironmentUtils.class.getSimpleName();
    // Int Values
    private static final int INT_UNDEFINED = -0x1;
    // Shell Commands
    private static final String CMD_PING_GOOGLE = "ping -c 1 google.com";

    //region [#] Public Static Methods
    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getMyDeviceIMEI(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return "";
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            /* getDeviceId() richiede il permesso "READ_PRIVILEGED_PHONE_STATE" che puo`essere richiesto solo dalle app di sistema.
                Teoricamente per le versioni di android < 29 non dovrebbe esserci nessun problema, in ogni caso per le versioni uguali o superiori viene ritornata una stringa vuota. */
            return telephonyManager != null ? telephonyManager.getDeviceId() : "";
        }
    }

    public static String getMyDeviceUniqueID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getMyDeviceName() {
        return DeviceName.getDeviceName();
    }

    public static String getMyAndroidRelease(){
        return Build.VERSION.RELEASE;
    }

    //region [#] App Methods
    public static String getMyAppVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0x0);
        return pi.versionName;
    }

    public static int getMyAppVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0x0);
        return pInfo.versionCode;
    }

    public static Integer findApkVersionCode(Context context, String path){
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(path, 0x0);
        if(info != null){
            return info.versionCode;
        } else {
            return null;
        }
    }

    public static void clearAppCache(@NonNull Context context){
        try {
            File cache = context.getCacheDir();
            FilesUtils.deleteDirContents(cache);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
    }
    //endregion

    //region [#] Memory Methods
    public static boolean isExternalMemoryAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableInternalMemorySize(){
        StatFs stat = new StatFs((Environment.getDataDirectory().getPath()));
        return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();         // Bytes
    }

    public static long getTotalInternalMemorySize(){
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return stat.getBlockSizeLong() & stat.getBlockCountLong();              // Bytes
    }

    public static long getAvailableExternalMemorySize(){
        if(isExternalMemoryAvailable()){
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            return INT_UNDEFINED;
        }
    }

    public static long getTotalExternalMemorySize(){
        if(isExternalMemoryAvailable()){
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            return stat.getBlockSizeLong() * stat.getBlockCountLong();
        } else {
            return INT_UNDEFINED;
        }
    }
    //emdregion

    //region [#] Storage & Files Methods
    public static String getPathApplicationDataDir(@NonNull Context context){
        return context.getApplicationInfo().dataDir;
    }

    public static String getPathApplicationFilesDir(@NonNull Context context){
        return context.getFilesDir().getPath();
    }

    public static String getPathApplicationExternalFilesDir(@NonNull Context context) {
        return context.getExternalFilesDir(null).getPath();
    }

    public static String getPathApplicationExternalFilesDir(@NonNull Context context, @Nullable String type) {
        return context.getExternalFilesDir(type).getPath();
    }
    //endregion

    //region [#] Connection Methods
    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    public static boolean isInternetAvailable(){
        try {
            int a = Runtime.getRuntime().exec(CMD_PING_GOOGLE).waitFor();
            return a == 0x0;
        } catch (IOException ioE){

        } catch (InterruptedException iE){

        }
        return false;
    }

    public static boolean isWifiConnected(@NonNull Context context){
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : Objects.requireNonNull(connectivityManager).getAllNetworks()) {
            final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == TYPE_WIFI) {
                return networkInfo.isConnected();
            }
        }
        return false;
    }

    public static boolean checkConnectionShowError(@NonNull Context context){   // Controlla se connessione dati o wifi sono attivi.
        boolean ret = true;
        if(!isConnected(context)) {
            Toast.makeText(context, context.getString(R.string.error_no_internet_only_local), Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }

    public static boolean checkInternetAvailableShowError(@NonNull Context context){    // Controlla se connessione dati o wifi sono attivi e fa un ping verso google per controllare che effettivamente ci sia connessione.
        boolean ret = true;
        if(!isConnected(context) || !isInternetAvailable()) {
            Toast.makeText(context, context.getString(R.string.error_no_internet_only_local), Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }

    public static int getTypeConnection(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork.getType();
        } else {
            return 0x0;
        }
    }
    //endregion
    //endregion

}