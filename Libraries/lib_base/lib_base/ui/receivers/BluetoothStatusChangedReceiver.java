package com.bodhitech.it.lib_base.lib_base.ui.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStatusChangedReceiver extends BroadcastReceiver {

    public interface IOnBluetoothStatusChanged {
        void onBluetoothStatusChanged(int status);
    }

    private IOnBluetoothStatusChanged mCallback;
    private boolean mIsRegistered;

    public BluetoothStatusChangedReceiver(IOnBluetoothStatusChanged callback){
        mCallback = callback;
        mIsRegistered = false;
    }

    public BluetoothStatusChangedReceiver(){
        mCallback = null;
        mIsRegistered = false;
    }

    /** Override BroadcastReceiver Methods **/
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            onReceiverBluetoothStatusChanged(context, intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR));
        }
    }

    /** Protected Methods **/
    protected void onReceiverBluetoothStatusChanged(Context context, int status){
        if(mCallback != null){
            mCallback.onBluetoothStatusChanged(status);
        }
    }

    /** Public Methods **/
    public void setIsRegistered(boolean registered){
        mIsRegistered = registered;
    }

    public boolean isRegistered(){
        return mIsRegistered;
    }

}
