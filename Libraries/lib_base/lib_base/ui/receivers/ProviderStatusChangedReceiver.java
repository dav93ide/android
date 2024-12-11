package com.bodhitech.it.lib_base.lib_base.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class ProviderStatusChangedReceiver extends BroadcastReceiver {

    public interface IOnProviderStatusChanged {
        int STATUS_ENABLED = 0x1;
        int STATUS_DISABLED = 0x0;

        void onProviderStatusChanged(int status);
    }

    private IOnProviderStatusChanged mCallback;
    private boolean mIsRegistered;

    public ProviderStatusChangedReceiver(IOnProviderStatusChanged callback){
        this();
        mCallback = callback;
    }

    public ProviderStatusChangedReceiver(){
        mIsRegistered = false;
    }

    /** Override BroadcastReceiver Methods **/
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction() != null && intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)){
            onReceiveProviderStatusChanged(context);
        }
    }

    /** Public Methods **/
    public boolean isRegistered(){
        return mIsRegistered;
    }

    public void setIsRegistered(boolean isRegistered){
        mIsRegistered = isRegistered;
    }

    /** Protected Methods **/
    protected void onReceiveProviderStatusChanged(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(lm != null && mCallback != null){
            mCallback.onProviderStatusChanged(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ?
                    IOnProviderStatusChanged.STATUS_ENABLED : IOnProviderStatusChanged.STATUS_DISABLED);
        }
    }

}
