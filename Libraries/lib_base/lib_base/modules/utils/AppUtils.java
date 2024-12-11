package com.bodhitech.it.lib_base.lib_base.modules.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.bodhitech.it.lib_base.lib_base.modules.managers.GetLocationManager;
import com.bodhitech.it.lib_base.R;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AppUtils {

    public interface IOnRequestCallback {
        void onGranted();
        void onDenied();
    }

    private static final String TAG = AppUtils.class.getSimpleName();
    // Logs
    private static final String LOG_ERROR_JOB_SCHEDULING = "Errore schedulazione Job '%1$s'";

    //region [#] Public Static Methods
    //region [#] Permissions Methods
    public static boolean checkRequestApplicationPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        List<String> permissionsRequired = checkGetApplicationPermissions(activity, permissions);
        if (permissionsRequired.size() > 0x0) {
            ActivityCompat.requestPermissions(activity, permissionsRequired.toArray(new String[0x0]), requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkApplicationPermissions(@NonNull Context context, @NonNull String[] permissions){
        return checkGetApplicationPermissions(context, permissions).size() == 0x0;
    }

    public static boolean askForPermissions(@NonNull Activity activity, @NonNull String permission, @NonNull String dialogTitle, @NonNull String dialogMsg, @NonNull String txtPositiveButton, @NonNull String txtNegativeButton, @NonNull IOnRequestCallback callback) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new AlertDialog.Builder(activity)
                    .setTitle(dialogTitle)
                    .setMessage(dialogMsg)
                    .setPositiveButton(txtPositiveButton, (dialog, which) -> callback.onGranted())
                    .setNegativeButton(txtNegativeButton, (dialog, which) -> callback.onDenied())
                    .show();
            return true;
        }
        return false;
    }

    public static boolean checkPermissionGranted(@NonNull Context context, @NonNull String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean[] checkApplicationGivenPermissions(@NonNull Context context, @NonNull String[] permissions){
        boolean[] given = new boolean[]{};
        if(permissions.length > 0x0) {
            given = new boolean[permissions.length];
            for(int i=0x0; i < permissions.length; i++){
                given[i] = ContextCompat.checkSelfPermission(context, permissions[i]) == PackageManager.PERMISSION_GRANTED;
            }
        }
        return given;
    }

    /**
     * Checks if GPS is enabled (no high precision) otherwise show dialog and request to enable it.
     *
     * @param activity          ->  Activity used as context and for the "OnActivityResult" callback method.
     * @param titleResID        ->  Dialog Title string resource id.
     * @param contentResID      ->  Dialog Content string resource id.
     * @param reqCodeActivity   ->  requestCode sent as param to the "OnActivityResult" callback method.
     * @return  true    ->  if gps is already enabled
     *          false   ->  otherwise and if user press ok it returns inside the OnActivityResult activity's callback method.
     */
    public static boolean showDialogEnableGpsRequest(@NonNull Activity activity, @StringRes int titleResID, @StringRes int contentResID, int reqCodeActivity, @Nullable IOnRequestCallback callback){
        LocationManager lM = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (lM != null && !lM.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new MaterialDialog.Builder(activity)
                        .autoDismiss(false)
                        .cancelable(false)
                        .title(titleResID)
                        .content(contentResID)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .onPositive(
                                (dialog, which) -> {
                                    dialog.cancel();
                                    if(callback != null){
                                        callback.onGranted();
                                    }
                                    GetLocationManager.initInstance(activity).requestEnableLocation(reqCodeActivity);
                                }
                        )
                        .onNegative(
                                (dialog, which) -> {
                                    dialog.cancel();
                                    if(callback != null){
                                        callback.onDenied();
                                    }
                                })
                        .autoDismiss(false)
                        .cancelable(false)
                        .show();
            } else {
                return true;
            }
        } else {
            Toast.makeText(activity, activity.getString(R.string.error_must_grant_permission_elements, activity.getString(R.string.label_of_position_access)), Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    //endregion

    //region [#] Application Status Methods
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0x0);
            return true;
        } catch (PackageManager.NameNotFoundException nnfE) {
            return false;
        }
    }

    public static boolean isAppOnBackgroundOrClosed(@NonNull Context context) {
        boolean background = true;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = Objects.requireNonNull(activityManager).getRunningAppProcesses();
        if(appProcesses != null) {
            String packageName = context.getPackageName();
            for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if(appProcess.processName.equals(packageName)) {
                    if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        background = false;
                    }
                }
            }
        }
        return background;
    }

    public static boolean isAppFromGooglePlayStore(@NonNull Context context){
        List<String> validInstallers = new ArrayList<>(Arrays.asList(BaseConstants.PCKG_ANDROID_VENDING, BaseConstants.PCKG_GOOGLE_FEEDBACK));
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }
    //endregion

    //region [#] Intent Methods
    /**
     * Apre un selezionatore che permette di avviare "Image Picker" o di fare una foto.
     * @param context
     * @param title Titolo del selezionatore.
     * @param fileProvider  File provider dell'app per ottenere l'URI del file dove verra`salvata la foto scattata.
     * @param dirType   Tipo di directory dei files dove salvare la foto.
     * @param filename  Nome del file dove salvare la foto.
     * @return  Intent Chooser
     */
    @Nullable
    public static Intent getChooserPickImageAndTakePhoto(@NonNull Context context, @NonNull String title, @NonNull String fileProvider, @NonNull String dirType, @NonNull String filename){
        Intent iPick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent iTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        iTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, FilesUtils.getNewFileUri(context, fileProvider, dirType, filename));

        List<Intent> iList = new ArrayList<>();
        DataUtils.addIntentToList(context, iList, iPick);
        DataUtils.addIntentToList(context, iList, iTakePhoto);

        Intent chooser = null;
        if(iList.size() > 0x0){
            chooser = Intent.createChooser(iPick, title);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, iList.toArray(new Parcelable[]{}));
        }
        return chooser;
    }

    /**
     * Apre un selezionatore che permette di avviare "Image Picker" (funzionalita`selezione immagini)
     * @param context
     * @param title Titolo del selezionatore.
     * @return  Intent Chooser
     */
    @Nullable
    public static Intent getChooserPickImage(@NonNull Context context, @NonNull String title ){
        Intent iPick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        List<Intent> iList = new ArrayList<>();
        DataUtils.addIntentToList(context, iList, iPick);
        Intent chooser = null;
        if(iList.size() > 0x0){
            chooser = Intent.createChooser(iPick, title);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, iList.toArray(new Parcelable[]{}));
        }
        return chooser;
    }
    //endregion

    //region [#] Activities & Services Methods
    public static void launchGooglePlayStore(@NonNull Context context, @NonNull String packageName){
        try{
            try{
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(BaseConstants.GOOGLE_PLAY_STORE_APP_URI, packageName))));
            } catch (ActivityNotFoundException anfE){
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(BaseConstants.GOOGLE_PLAY_STORE_WEB_URL, packageName))));
            }
        } catch (Exception e){
            Toast.makeText(context, R.string.error_cannot_open_google_play, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean launchApplicationFromPackage(@NonNull Context context, @NonNull String packageName, @Nullable String... extras) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            if(extras != null){
                if(extras.length % 0x2 == 0x0) {
                    for (int i=0x0; i < extras.length; i+=0x2){
                        launchIntent.putExtra(extras[i], extras[i+0x1]);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void launchCamera(@NonNull Activity activity, @NonNull String destFile, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(destFile)));
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(takePictureIntent, requestCode);
            }
        } else {
            Toast.makeText(activity, R.string.error_permission_camera, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean startGoogleMapsActivity(@NonNull Context context, double latitude, double longitude){
        try{
            Uri gmmIntentUri = Uri.parse(BaseConstants.GOOGLE_MAP_NAVIGATION_URI + latitude + "," + longitude);
            context.startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage(BaseConstants.PCKG_GOOGLE_MAPS));
            return true;
        } catch (ActivityNotFoundException anfE){
            BaseEnvironment.onExceptionLevelLow(TAG, anfE);
        }
        return false;
    }

    public static <T extends Service> boolean isMyServiceRunning(@NonNull Context context, @NonNull Class<T> serviceClass) {
        boolean ret = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    ret = true;
                }
            }
        }
        return ret;
    }
    //endregion

    //region [#] Job Scheduling Methods
    public static <T extends Service> boolean scheduleLatencyJob(@NonNull Context context, @NonNull Class<T> jobClass, int jobID, long jobTime){
        return scheduleLatencyJob(context, jobClass, jobID, jobTime, null);
    }

    public static <T extends Service> boolean scheduleLatencyJob(@NonNull Context context, @NonNull Class<T> jobClass, int jobID, long jobTime, @Nullable PersistableBundle jobExtras){
        if(jobExtras == null) {
            jobExtras = new PersistableBundle();
        }
        jobExtras.putLong(BaseConstants.EXTRA_JOB_TIMING, jobTime);
        return scheduleJob(context, jobClass, jobID, jobExtras, (builder) -> builder.setMinimumLatency(jobTime));
    }

    public static <T extends Service> boolean schedulePeriodicJob(@NonNull Context context, @NonNull Class<T> jobClass, int jobID, long jobTime){
        return schedulePeriodicJob(context, jobClass, jobID, jobTime, null);
    }

    public static <T extends Service> boolean schedulePeriodicJob(@NonNull Context context, @NonNull Class<T> jobClass, int jobID, long jobTime, @Nullable PersistableBundle jobExtras){
        if(jobExtras == null) {
            jobExtras = new PersistableBundle();
        }
        jobExtras.putLong(BaseConstants.EXTRA_JOB_TIMING, jobTime);
        return scheduleJob(context, jobClass, jobID, jobExtras, (builder -> builder.setPeriodic(jobTime)));
    }

    public static <T extends Service> void enqueueWork(@NonNull Context context, @NonNull Class<T> jobClass, int jobID){
        JobIntentService.enqueueWork(context, jobClass, jobID, new Intent(context, jobClass));
    }

    public static <T extends Service> void enqueueWork(@NonNull Context context, @NonNull Class<T> jobClass, int jobID, @NonNull Intent work){
        JobIntentService.enqueueWork(context, jobClass, jobID, work);
    }

    public static boolean isMyJobScheduled(@NonNull Context context, int jobID) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(scheduler != null){
            List<JobInfo> jobs = scheduler.getAllPendingJobs();
            for(JobInfo job : jobs){
                if(job.getId() == jobID){
                    return true;
                }
            }
        }
        return false;
    }

    public static void cancelScheduledJob(@NonNull Context context, int jobID){
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(scheduler != null){
            scheduler.cancel(jobID);
        }
    }
    //endregion

    //region [#] Notification Methods
    public static void notifyNotification(@NonNull Context context, int idNotification, @NonNull Notification notification){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.notify(idNotification, notification);
        }
    }

    public static void deleteNotification(@NonNull Context context, int idNotification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null) {
            notificationManager.cancel(idNotification);
        }
    }

    public static void showLowImportanceNotification(@NonNull Context context, int idNotification, @NonNull Notification notification, @NonNull String channelID, @NonNull String channelName, @NonNull String channelDescription){
        showNotification(context, idNotification, notification, channelID, channelName, channelDescription, NotificationManager.IMPORTANCE_LOW);
    }

    public static void showNotification(@NonNull Context context, int idNotification, @NonNull Notification notification, @NonNull String channelID, @NonNull String channelName, @NonNull String channelDescription, int channelImportance){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupNotificationChannel(context, channelID, channelName, channelDescription, channelImportance);
        }
        notifyNotification(context, idNotification, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel initLowImportanceNotificationChannel(@NonNull String channelID, @NonNull String channelName, @NonNull String channelDescription){
        return initNotificationChannel(channelID, channelName, channelDescription, NotificationManager.IMPORTANCE_LOW);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel initNotificationChannel(@NonNull String channelID, @NonNull String channelName, @NonNull String channelDescription, int importance){
        NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
        channel.setDescription(channelDescription);
        return channel;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setupLowImportanceNotificationChannel(@NonNull Context context, @NonNull String channelID, @NonNull String channelName, @NonNull String channelDescription){
        setupNotificationChannel(context, channelID, channelName, channelDescription, NotificationManager.IMPORTANCE_LOW);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setupNotificationChannel(@NonNull Context context, @NonNull String channelID, @NonNull String channelName, @NonNull String channelDescription, int importance){
        NotificationManager nM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nM != null && nM.getNotificationChannel(channelID) == null){
            nM.createNotificationChannel(initNotificationChannel(channelID, channelName, channelDescription, importance));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setupSilentNotificationChannel(@NonNull  Context context, @NonNull String idChannel, @NonNull String nameChannel, @NonNull String descChannel){
        NotificationManager nM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nM != null) {
            nM.deleteNotificationChannel(idChannel);
            NotificationChannel channel = AppUtils.initNotificationChannel(idChannel, nameChannel, descChannel, NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            channel.setSound(null, null);
            nM.createNotificationChannel(channel);
        }
    }
    //endregion

    //region [#] Vibration & Ringtone Methods
    public static void doOneShotVibration(@NonNull Context context, long vibrationMillis, int vibrationAmplitude){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(vibrationMillis, vibrationAmplitude));
            } else {
                vibrator.vibrate(vibrationMillis);
            }
        }
    }

    public static void doWaveFormVibration(@NonNull Context context, long[] timings, int[] amplitudes, int repeat){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeat));
            } else {
                vibrator.vibrate(timings, repeat);
            }
        }
    }

    public static void startVibration(@NonNull Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator != null && vibrator.hasVibrator()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                long[] l = new long[]{500, 110, 500, 110, 500};
                int[] i = new int[]{255, 0, 255, 0, 255};
                long[] ll = ArrayUtils.addAll(l, l);
                int[] ii = ArrayUtils.addAll(i, i);
                vibrator.vibrate(VibrationEffect.createWaveform(ll, ii,-0x1));
            } else {
                vibrator.vibrate(3000);
            }
        }
    }

    public static Ringtone playRingtone(@NonNull Context context, int type){
        Uri ringtoneUri = RingtoneManager.getDefaultUri(type);
        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        ringtone.play();
        return ringtone;
    }
    //endregion

    //region [#] Application Features (Battery, Widget, Sms, ...) Methods
    public static Intent getBatteryStatus(@NonNull Context context){
        return context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public static void updateWidget(@NonNull Context context, @NonNull Class<?> widgetClass, int xmlResource) {
        context.sendBroadcast(new Intent(context, widgetClass)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{xmlResource}));
    }

    public static boolean makeShortcut(@NonNull Context context, @NonNull String id, @NonNull Class<?> activity, @NonNull String label, @DrawableRes int icon){
        ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, id)
                .setIntent(new Intent(context, activity).setAction(Intent.ACTION_MAIN))
                .setShortLabel(label)
                .setIcon(IconCompat.createWithResource(context, icon))
                .build();
        return ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);
    }
    //endregion

    //region [#] Application Components Methods
    public static boolean checkIfComponentIsEnabled(@NonNull Context context, @NonNull Class<?> componentClass){
        boolean ret = false;
        ComponentName componentName = new ComponentName(context, componentClass);
        int componentEnabled = context.getPackageManager().getComponentEnabledSetting(componentName);
        switch (componentEnabled) {
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
                break;
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                ret = true;
                break;
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
            default:
                ret = checkEnabledComponentsInfo(context, componentClass);
                break;
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED:
                break;
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER:
                break;
        }
        return ret;
    }

    public static void enableDisableComponent(@NonNull Context context, @NonNull Class<?> componentClass, boolean enable){
        ComponentName component = new ComponentName(context, componentClass);
        if(enable == !checkIfComponentIsEnabled(context, componentClass)) {
            context.getPackageManager().setComponentEnabledSetting(
                    component,
                    enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
    //endregion
    //endregion

    //region [#] Private Static Methods
    private static List<String> checkGetApplicationPermissions(@NonNull Context context, @NonNull String[] permissions){
        List<String> permissionsRequired = new ArrayList<>();
        if (permissions.length > 0x0) {
            for (String permission : permissions) {
                if (permission != null && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsRequired.add(permission);
                }
            }
        }
        return permissionsRequired;
    }

    //region [#] Job Scheduling Methods
    @FunctionalInterface
    private interface IJobInfoBuilderTiming {
        void setTiming(JobInfo.Builder builder);
    }

    private static <T extends Service> boolean scheduleJob(@NonNull Context context, @NonNull Class<T> jobClass, int jobID, @Nullable PersistableBundle jobExtras, @Nullable IJobInfoBuilderTiming jobTiming){
        ComponentName serviceComponent = new ComponentName(context, jobClass);
        JobInfo.Builder builder = new JobInfo
                .Builder(jobID, serviceComponent)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        if(jobExtras != null){
            builder.setExtras(jobExtras);
        }
        if(jobTiming != null) {
            jobTiming.setTiming(builder);
        }
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(scheduler != null){
            int res = scheduler.schedule(builder.build());
            switch(res){
                case JobScheduler.RESULT_SUCCESS:
                    return true;
                case JobScheduler.RESULT_FAILURE:
                default:
                    BaseEnvironment.getLogger().e(TAG, String.format(LOG_ERROR_JOB_SCHEDULING, jobClass.getSimpleName()));
                    return false;
            }
        } else {
            BaseEnvironment.getLogger().e(TAG, String.format(LOG_ERROR_JOB_SCHEDULING, jobClass.getSimpleName()));
            return false;
        }
    }
    //endregion

    //region [#] Application Components Methods
    private static boolean checkEnabledComponentsInfo(@NonNull Context context, @NonNull Class<?> componentClass){
        boolean ret = false;
        try{
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_RECEIVERS |
                    PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_DISABLED_COMPONENTS);
            List<ComponentInfo> componentsInfo = new ArrayList<>();
            if(packageInfo.activities != null && packageInfo.activities.length > 0x0){
                Collections.addAll(componentsInfo, packageInfo.activities);
            }
            if(packageInfo.services != null && packageInfo.services.length > 0x0){
                Collections.addAll(componentsInfo, packageInfo.services);
            }
            if(packageInfo.providers != null && packageInfo.providers.length > 0x0){
                Collections.addAll(componentsInfo, packageInfo.providers);
            }
            if(packageInfo.receivers != null && packageInfo.receivers.length > 0x0){
                Collections.addAll(componentsInfo, packageInfo.receivers);
            }
            if(componentsInfo.size() > 0x0){
                for(ComponentInfo info : componentsInfo){
                    if(info.name.equals(componentClass.getName())){
                        ret = info.isEnabled();
                        break;
                    }
                }
            }
        } catch(PackageManager.NameNotFoundException nnfE){
            BaseEnvironment.onExceptionLevelLow(TAG, nnfE);
        }
        return ret;
    }
    //endregion
    //endregion

}
