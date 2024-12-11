package com.bodhitech.it.lib_base.lib_base.modules.update;

import android.annotation.SuppressLint;
import android.content.IntentSender;

import androidx.activity.ComponentActivity;
import androidx.annotation.ColorInt;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.bodhitech.it.lib_base.R;

public class InAppUpdateManager implements LifecycleObserver {

    private static final String TAG = InAppUpdateManager.class.getSimpleName();
    // Requests Codes
    private static final int REQ_CODE_FLEXIBLE_UPDATE = 0xF1;
    private static final int REQ_CODE_IMMEDIATE_UPDATE = 0xF2;

    private ComponentActivity mActivity;

    private AppUpdateManager mUpdateManager;

    private Snackbar mSnackbar;
    private InstallStateUpdatedListener mUpdatedListener;
    private boolean mUpdateChecked;

    public static InAppUpdateManager initInstance(ComponentActivity activity){
        return new InAppUpdateManager(activity);
    }

    private InAppUpdateManager(ComponentActivity activity){
        mActivity = activity;
        initSnackbar();
        initAppUpdateManager();
        mActivity.getLifecycle().addObserver(this);
        mUpdateChecked = false;
    }

    /** Builder Methods **/
    public InAppUpdateManager setActionTextColorSnackbar(@ColorInt int color){
        mSnackbar.setActionTextColor(color);
        return this;
    }

    /** Public Methods **/
    public void checkInAppUpdate(){
        if(!mUpdateChecked){
            mUpdateChecked = true;
            mUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
                switch (result.updateAvailability()){
                    case UpdateAvailability.UPDATE_NOT_AVAILABLE:
                        mUpdateChecked = false;
                        break;
                    case UpdateAvailability.UPDATE_AVAILABLE:
                        if(result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                            startFlexibleUpdate(result);
                            initInstallStateUpdatedListener();
                            mUpdateManager.registerListener(mUpdatedListener);
                        } else if(result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                            startImmediateUpdate(result);
                        }
                        break;
                    case UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS:
                        startImmediateUpdate(result);
                        break;
                }
            });
        }
    }

    /** Lifecycle Methods **/
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        checkNewAppVersionState();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if(mUpdateManager != null && mUpdatedListener != null){
            mUpdateManager.unregisterListener(mUpdatedListener);
        }
    }

    /** Private Methods **//** Init Methods **/
    private void initAppUpdateManager(){
        mUpdateManager = AppUpdateManagerFactory.create(mActivity);
        /*mUpdateManager = new FakeAppUpdateManager(mActivity);
        mUpdateManager.setUpdateAvailable(200);
        mUpdateManager.setUpdatePriority(AppUpdateType.FLEXIBLE);*/
    }

    @SuppressLint("WrongConstant")
    private void initSnackbar(){
        mSnackbar = Snackbar.make(mActivity.getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
    }

    /** Listeners Methods **/
    private void initInstallStateUpdatedListener(){
        mUpdatedListener = state -> {
            switch (state.installStatus()){
                case InstallStatus.INSTALLED:
                    showSnackbarInstallComplete();
                    break;
                case InstallStatus.DOWNLOADED:
                    showSnackbarDownloadUpdateCompleted();
                    break;
                case InstallStatus.FAILED:
                    showSnackbarRetryUpdate();
                    break;
                case InstallStatus.DOWNLOADING:
                    showSnackbarDownloading(state.bytesDownloaded(), state.totalBytesToDownload());
                    break;
            }
        };
    }

    /** Updates Methods **/
    private void startFlexibleUpdate(AppUpdateInfo info){
        try {
            mUpdateManager.startUpdateFlowForResult(info, AppUpdateType.FLEXIBLE, mActivity, REQ_CODE_FLEXIBLE_UPDATE);
        } catch (IntentSender.SendIntentException siE){
            BaseEnvironment.onExceptionLevelLow(TAG, siE);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
    }

    private void startImmediateUpdate(AppUpdateInfo info){
        try {
            mUpdateManager.startUpdateFlowForResult(info, AppUpdateType.IMMEDIATE, mActivity, REQ_CODE_IMMEDIATE_UPDATE);
        } catch (IntentSender.SendIntentException siE){
            BaseEnvironment.onExceptionLevelLow(TAG, siE);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
    }

    private void checkNewAppVersionState(){
        mUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
            if (result.installStatus() == InstallStatus.DOWNLOADED) {
                if (!mSnackbar.isShown()) {
                    showSnackbarDownloadUpdateCompleted();
                }
            }
        });
    }

    /** SnackBar Methods **/
    @SuppressLint("WrongConstant")
    private void showSnackbarRetryUpdate(){
        mSnackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setText(mActivity.getString(R.string.error_download_element, mActivity.getString(R.string.label_updating)));
        mSnackbar.setAction(mActivity.getString(R.string.label_retry_upper), view -> checkInAppUpdate());
        mSnackbar.show();
    }

    @SuppressLint("WrongConstant")
    private void showSnackbarDownloadUpdateCompleted(){
        mSnackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setText(mActivity.getString(R.string.success_download_element_m, mActivity.getString(R.string.label_updating)));
        mSnackbar.setAction(mActivity.getString(R.string.label_install_upper), view -> {
            mUpdateManager.completeUpdate();
        });
        mSnackbar.show();
    }

    @SuppressLint("WrongConstant")
    private void showSnackbarDownloading(long bytesDownloaded, long bytesTot){
        mSnackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setText(mActivity.getString(R.string.progress_download_element,
                mActivity.getString(R.string.label_of_updating) + (bytesTot != 0x0 ?
                        " (" + ((bytesDownloaded * 100) / bytesTot) + "%) " : "")));
        mSnackbar.show();
    }

    @SuppressLint("WrongConstant")
    private void showSnackbarInstallComplete(){
        mSnackbar.setDuration(Snackbar.LENGTH_SHORT);
        mSnackbar.setText(mActivity.getString(R.string.success_install));
        mSnackbar.show();
    }

}