package com.bodhitech.it.lib_base.lib_base.modules.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bodhitech.it.lib_base.lib_base.BaseConstants;
import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.bodhitech.it.lib_base.lib_base.modules.models.Position;
import com.bodhitech.it.lib_base.lib_base.modules.utils.AppUtils;
import com.bodhitech.it.lib_base.lib_base.modules.utils.PositionUtils;

public class GetLocationManager implements
        LifecycleObserver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public interface OnLocationUpdateCallback {
        /**
         * @param   location != null -> getLocationSuccess<br/>
         *          location == null -> getLocationFail
         */
        void onLocationUpdated(@Nullable Location location);
    }

    private static final String TAG = GetLocationManager.class.getSimpleName();
    // Requests Codes - Permissions
    public static final int REQ_CODE_PERMISSION_ACCESS_FINE_LOCATION = 0xFF00;
    // Requests Codes - Activities
    public static final int REQ_CODE_ACTIVITY_ENABLE_GPS = 0xFFA0;
    // Millis Interval
    private static final long MILLIS_FASTEST_INTERVAL = BaseConstants.MILLIS_ONE_SECOND * 0x5;
    private static final long MILLIS_INTERVAL = BaseConstants.MILLIS_ONE_SECOND * 40;

    private OnLocationUpdateCallback mCallback;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Activity mActivity;
    private boolean mSingleLocationUpdate;
    private boolean mStartLocationUpdateAfterConnected;

    public static GetLocationManager initInstance(@NonNull Activity activity){
        GetLocationManager mInstance = new GetLocationManager(activity);
        mInstance.initGoogleApiClient();
        return mInstance;
    }

    private GetLocationManager(@NonNull Activity activity){
        mActivity = activity;
        mSingleLocationUpdate = false;
        mStartLocationUpdateAfterConnected = false;
        if(mActivity instanceof AppCompatActivity){
            ((AppCompatActivity) mActivity).getLifecycle().addObserver(this);
        }
        initGoogleApiClient();
        initLocationRequest();
        initLocationSettingsRequest();
        initLocationCallback();
    }

    /** LifecycleObserver Methods **/
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartEvent(){
        connectGoogleApiClient();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStopEvent(){
        disconnectGoogleApiClient();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroyEvent(){
        if(mActivity instanceof AppCompatActivity) {
            ((AppCompatActivity)mActivity).getLifecycle().removeObserver(this);
        }
        mActivity = null;
        mCallback = null;
    }

    /** Override GoogleApiClient.ConnectionCallbacks Methods **/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(mStartLocationUpdateAfterConnected){
            startLocationUpdates();
        }
        mStartLocationUpdateAfterConnected = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /** Override GoogleApiClient.OnConnectionFailedListener Methods **/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /** Override Methods **/
    @Override
    public void onLocationChanged(Location location) {

    }

    /** Public Methods - Getter Methods **/
    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public LocationRequest getLocationRequest(){
        return mLocationRequest;
    }

    /** Setter Methods **/
    public GetLocationManager setCallback(OnLocationUpdateCallback callback){
        mCallback = callback;
        return this;
    }

    public GetLocationManager setSingleLocationUpdate(){
        mSingleLocationUpdate = true;
        /*
            NON FUNZIONA: facendo "setNumUpdates(0x1)" non funziona, non chiama mai la callback quindi non recupera mai la posizione!
                            Mettendo un valore > 0x1 funziona, ma per sicurezza tolgo direttamente questa parte di codice e uso la variabile "mSingleLocationUpdate" per forzare l'update singolo.

            mLocationRequest.setNumUpdates(0x1);      // IMPORTANTE: non e` detto che il singolo update funzioni sempre, a volte potrebbe riprendere di nuovo la posizione anche se l'ha gia` presa una volta.
        */
        return this;
    }

    public GetLocationManager setLocationRequestNumUpdates(int num){
        mLocationRequest.setNumUpdates(num);
        return this;
    }

    /** Google API Methods **/
    public void connectGoogleApiClient(){
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    public void disconnectGoogleApiClient(){
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    /** Check Methods **/
    public boolean isGoogleApiClientConnected(){
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    /** Location Methods **/
    public void requestEnableLocation(final int requestCode){
        checkLocationEnabled(requestCode);
    }

    public void startLocationUpdates(){
        mStartLocationUpdateAfterConnected = true;
        if(checkConnectGoogleApiClient() && checkRequestPermission() && checkRequestGpsAndNetworkEnabled()){
            try {
                LocationServices.getFusedLocationProviderClient(mActivity).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } catch (SecurityException sE){
                BaseEnvironment.onExceptionLevelLow(TAG, sE);
            } catch (Exception e){
                BaseEnvironment.onExceptionLevelLow(TAG, e);
            }
        }
    }

    public void stopLocationUpdates(){
        mStartLocationUpdateAfterConnected = false;
        try {
            LocationServices.getFusedLocationProviderClient(mActivity).removeLocationUpdates(mLocationCallback);
        } catch (SecurityException sE){
            BaseEnvironment.onExceptionLevelLow(TAG, sE);
        } catch (Exception e){
            BaseEnvironment.onExceptionLevelLow(TAG, e);
        }
    }

    /** Private Methods **//** Init Methods **/
    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        connectGoogleApiClient();
    }

    private void initLocationRequest(){
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(MILLIS_INTERVAL)
                .setFastestInterval(MILLIS_FASTEST_INTERVAL);
    }

    private void initLocationSettingsRequest(){
        mLocationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true)
                .build();
    }

    private void initLocationCallback(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(mSingleLocationUpdate){
                    stopLocationUpdates();
                }
                Location location = locationResult.getLastLocation();
                PositionUtils.setLastUserPosition(new Position(location.getLatitude(), location.getLongitude()));
                if(mCallback != null){
                    mCallback.onLocationUpdated(location);
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if(!locationAvailability.isLocationAvailable() && mCallback != null){
                    mCallback.onLocationUpdated(null);
                }
            }
        };
    }

    private void checkLocationEnabled(final int requestCode){
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest).setResultCallback(
                result -> {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(mActivity, requestCode);
                            } catch (IntentSender.SendIntentException e) {
                                openGpsEnabledSettings(requestCode);
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            openGpsEnabledSettings(requestCode);
                            break;
                    }
                }
        );
    }

    /** Check Methods **/
    private boolean checkConnectGoogleApiClient(){
        if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
            return true;
        } else {
            connectGoogleApiClient();
            return false;
        }
    }

    private boolean checkRequestPermission(){
        return AppUtils.checkRequestApplicationPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE_PERMISSION_ACCESS_FINE_LOCATION);
    }

    private boolean checkRequestGpsAndNetworkEnabled(){
        if(mActivity != null){
            LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
            if(lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                return true;
            } else {
                checkLocationEnabled(REQ_CODE_ACTIVITY_ENABLE_GPS);
            }
        }
        return false;
    }

    /** GPS Methods **/
    private void openGpsEnabledSettings(final int requestCode){
        mActivity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode);
    }

}

/* region - GetLocationManager utilizzando LocationRequest.

public class GetLocationManager implements OnCompleteListener<LocationSettingsResponse> {

    // Millis Interval
    private static final long MILLIS_FASTEST_INTERVAL = 1000L;
    private static final long MILLIS_INTERVAL = MILLIS_FASTEST_INTERVAL * 0xA;

    private Activity mActivity;
    private int mRequestCode;

    public static void requestEnableLocation(Activity activity, int requestCode){
        new GetLocationManager(activity, requestCode).requestEnableLocation();
    }

    private GetLocationManager(Activity activity, int requestCode){
        mActivity = activity;
        mRequestCode = requestCode;
    }

    /** Override OnCompleteListener<LocationSettingsResponse> Methods **\/
    @Override
    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
        try {
            task.getResult(ApiException.class);
        } catch (ApiException ex){
            switch (ex.getStatusCode()){
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ((ResolvableApiException) ex).startResolutionForResult(mActivity, mRequestCode);
                    } catch (IntentSender.SendIntentException e) {
                        openGpsEnabledSettings();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    openGpsEnabledSettings();
                    break;
            }
        }
    }

    /** Private Methods **\/
    private void requestEnableLocation(){
        getSettingsClient(mActivity)
                .checkLocationSettings(getLocationSettingsRequest())
                .addOnCompleteListener(this);
    }

    private LocationRequest getLocationRequest(){
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(MILLIS_INTERVAL)
                .setFastestInterval(MILLIS_FASTEST_INTERVAL);
    }

    private LocationSettingsRequest getLocationSettingsRequest(){
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest())
                .setAlwaysShow(true)
                .build();
    }

    private void openGpsEnabledSettings(){
        mActivity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), mRequestCode);
    }

}

endregion */

/* region - GetLocationManager usando GoogleApiClient & LocationServices

public class GetLocationManager implements

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Millis Interval
    private static final long MILLIS_FASTEST_INTERVAL = Constants.MILLIS_ONE_SECOND * 0x5;
    private static final long MILLIS_INTERVAL = Constants.TIMEOUT_GET_POSITION;

    private GoogleApiClient mGoogleApiClient;
    private Activity mActivity;
    private int mRequestCode;

    public static void requestEnableLocation(Activity activity, int requestCode){
        GetLocationManager instance = new GetLocationManager(activity, requestCode);
        instance.initGoogleApiClient();
        instance.initLocationSettingResultCallback();
    }

    private GetLocationManager(Activity activity, int requestCode){
        mActivity = activity;
        mRequestCode = requestCode;
    }

    /** Override GoogleApiClient.ConnectionCallbacks Methods **\/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /** Override GoogleApiClient.OnConnectionFailedListener Methods **\/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /** Private Methods **\/
    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationSettingResultCallback(){
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, getLocationSettingsRequest()).setResultCallback(
                result -> {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(mActivity, mRequestCode);
                            } catch (IntentSender.SendIntentException e) {
                                openGpsEnabledSettings();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            openGpsEnabledSettings();
                            break;
                    }
                }
        );
    }

    private void requestEnableLocation(){
        getSettingsClient(mActivity)
                .checkLocationSettings(getLocationSettingsRequest());
    }

    private LocationRequest getLocationRequest(){
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(MILLIS_INTERVAL)
                .setFastestInterval(MILLIS_FASTEST_INTERVAL);
    }

    private LocationSettingsRequest getLocationSettingsRequest(){
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest())
                .setAlwaysShow(true)
                .build();
    }

    private void openGpsEnabledSettings(){
        mActivity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), mRequestCode);
    }

}

endregion */