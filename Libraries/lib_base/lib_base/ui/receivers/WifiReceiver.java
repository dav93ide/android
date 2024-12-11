package com.bodhitech.it.lib_base.lib_base.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

public class WifiReceiver extends BroadcastReceiver {

    public interface IOnWifiStatusChanged {
        int WIFI_CONNECTED = 0x1;
        int WIFI_DISCONNECTED = 0x2;

        void onWifiStatusChanged(int status);
    }

    private static final String TAG = WifiReceiver.class.getSimpleName();
    // Action
    public static final String ACTION_WIFI_NETWORK_STATE_CHANGED = WifiManager.NETWORK_STATE_CHANGED_ACTION;
    // Data
    public static final String EXTRA_WIFI_NETWORK_INFO = WifiManager.EXTRA_NETWORK_INFO;
    public static final String EXTRA_WIFI_BSSID = WifiManager.EXTRA_BSSID;
    public static final String EXTRA_WIFI_INFO = WifiManager.EXTRA_WIFI_INFO;

    private IOnWifiStatusChanged mCallback;
    private int mWifiStatus;
    private boolean isRegistered;

    public WifiReceiver(){}

    public WifiReceiver(@NonNull IOnWifiStatusChanged callback){
        mCallback = callback;
        isRegistered = false;
        mWifiStatus = 0x0;
    }

    /** Override BroadcastReceiver Methods **/
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction() != null){
            String action = intent.getAction();
            switch(action){
                case ACTION_WIFI_NETWORK_STATE_CHANGED:
                    onWifiNetworkStateChanged(intent);
                    break;
            }
        }
    }

    /** Getter & Setter Methods **/
    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    /** Private Methods **/
    private void onWifiNetworkStateChanged(Intent intent){
        NetworkInfo netInfo = intent.getParcelableExtra(EXTRA_WIFI_NETWORK_INFO);
        String wifiBSSID = intent.getStringExtra(EXTRA_WIFI_BSSID);
        WifiInfo wifiInfo = intent.getParcelableExtra(EXTRA_WIFI_INFO);
        if(netInfo!= null){
            switch(netInfo.getState()){
                case CONNECTED:
                    if(mCallback != null && /*wifiInfo != null && wifiBSSID != null &&*/
                       (mWifiStatus == 0x0 || mWifiStatus == IOnWifiStatusChanged.WIFI_DISCONNECTED)){
                        mCallback.onWifiStatusChanged(IOnWifiStatusChanged.WIFI_CONNECTED);
                        mWifiStatus = IOnWifiStatusChanged.WIFI_CONNECTED;
                    }
                    break;
                case DISCONNECTED:
                    if(mCallback != null && /*wifiInfo == null && wifiBSSID == null &&*/
                        (mWifiStatus == 0x0 || mWifiStatus == IOnWifiStatusChanged.WIFI_CONNECTED)){
                        mCallback.onWifiStatusChanged(IOnWifiStatusChanged.WIFI_DISCONNECTED);
                        mWifiStatus = IOnWifiStatusChanged.WIFI_DISCONNECTED;
                    }
                    break;
            }
        }
    }

}
