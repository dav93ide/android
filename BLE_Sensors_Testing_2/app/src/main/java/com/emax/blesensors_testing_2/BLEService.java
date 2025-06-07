package com.emax.blesensors_testing_2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.IBeacon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BLEService extends Service {

    private static final int SCAN_DELAY_MILLIS = 20000;
    private static final int SCAN_TIME_MILLIS = 5000;
    private static final int MAX_EXTENSIVE_SCANS = 50;
    private static final String[] MY_MACS = new String[]{
        //"00:15:85:10:85:3C"
        "00:15:83:00:8F:CB"
    };
    private static final String FILE_PATH = "/";
    private static final String FILE_RSSI = "rssi";
    private static final String FILE_INFO = "info";
    private static final String FILE_WARNINGS = "warnings";
    private static final String FILE_STATUS = "status";

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private ScanCallback mScanCallback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mGattCallback;
    private HashMap<String, Integer> mTxpower;
    private int nMis;
    private boolean isScanning;
    private String mTextRssi;
    private String mTextInfo;
    private double mMaxDistance;

    /* START Override Lifecycle Methods */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initAll();
        startScan();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /* END Override Lifecycle Methods */

    /* START Private Methods *//* START Init Methods */
    private void initAll(){
        mMaxDistance = 1.5d;
        initBluetoothAdapter();
        initBluetoothLeScanner();
        initBluetoothScanCallback();
        initGattCallback();
        initLightSensor();
        mHandler = new Handler();
        isScanning = false;
    }

    private void initTxPower(){
        mTxpower = new HashMap<>();
    }

    private void initBluetoothAdapter(){
        final BluetoothManager bluetoothManager = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void initBluetoothLeScanner(){
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    private void initBluetoothScanCallback(){
        final Context mContext = this;
        mScanCallback = new ScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                String name = device.getName();
                if(Arrays.asList(MY_MACS).contains(device.getAddress())){
                    List<ADStructure> structures = ADPayloadParser.getInstance().parse(result.getScanRecord().getBytes());
                    if(structures != null && structures.size() > 0){
                        for(ADStructure st : structures){
                            if(st instanceof IBeacon){
                                IBeacon beacon = (IBeacon) st;
                                mTxpower.put(device.getAddress(), beacon.getPower());
                            }
                        }
                    }
                    device.connectGatt(mContext, true, mGattCallback);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
    }

    private void initGattCallback(){
        final Context mContext = this;
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    if(newState == BluetoothGatt.STATE_CONNECTED){
                        gatt.readRemoteRssi();
                        if(mBluetoothGatt == null){
                            mBluetoothGatt = gatt;
                        }
                    }
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                onReadRSSI(mContext, gatt, status, rssi);
            }
        };
    }

    private void initLightSensor(){
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        SensorEventListener lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch(sensorEvent.sensor.getType()){
                    case Sensor.TYPE_LIGHT:
                        if(sensorEvent.values[0] == 0.0f){
                            mMaxDistance = 4.0d;
                        } else {
                            mMaxDistance = 1.5d;
                        }
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(
            lightSensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        );
    }
    /* END Init Methods */

    /* START Synchronized Methods */
    synchronized private void incrementNMis(){
        nMis++;
    }

    synchronized private boolean checkNMis(){
        return nMis <= MAX_EXTENSIVE_SCANS;
    }

    synchronized  private void onReadRSSI(Context mContext, BluetoothGatt gatt, int status, int rssi){
        if(status == BluetoothGatt.GATT_SUCCESS){
            incrementNMis();
            double distance = getDistance(mTxpower.get(MY_MACS[0]), rssi);
            mTextRssi = String.valueOf(rssi) + "\n";
            mTextInfo =    "####################################\n"
                +   "# Distance: " + String.valueOf(distance) + "\n"
                +   "# RSSI: " + String.valueOf(rssi) + "\n"
                +   "# nMis: " + String.valueOf(nMis) + "\n"
                +   "###################################\n\n\n";
            FileUtils.writeToFile(mContext, FILE_RSSI, FILE_PATH, mTextRssi, true);
            FileUtils.writeToFile(mContext, FILE_INFO, FILE_PATH, mTextInfo, true);
            mTextRssi = "";
            mTextInfo = "";
            if(checkNMis()){
                gatt.readRemoteRssi();
            } else {
                gatt.disconnect();
                doTheMagic(mContext);
                mScanner.stopScan(mScanCallback);
                isScanning = false;
            }
        }
    }
    /* END Synchronized Methods */

    private void startScan(){
        nMis = 0;
        if(!isScanning) {
            initTxPower();
            isScanning = true;
            mScanner.startScan(mScanCallback);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_TIME_MILLIS);
    }

    private void stopScan(){
        if(isScanning) {
            isScanning = false;
            if(mBluetoothGatt != null){
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            }
            mScanner.stopScan(mScanCallback);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        }, SCAN_DELAY_MILLIS);
        checkDeviceFound();
    }

    private void  doTheMagic(Context mContext){
        String fileText = FileUtils.readFromFile(this, FILE_RSSI, FILE_PATH, true);
        if(fileText != null && !fileText.isEmpty()){
            String[] splitted = fileText.split("\n");
            if(splitted != null && splitted.length > 0){
                Integer tot = 0;
                for(String txt : splitted){
                    Integer value = Integer.parseInt(txt);
                    tot += value;
                }
                Integer average = tot / splitted.length;
                double distance = getDistance(mTxpower.get(MY_MACS[0]), average);
                String text =   "####################################\n"
                            +   "# Media RSSI: " + String.valueOf(average) + "\n"
                            +   "# Distanza: " + String.valueOf(distance) + "\n"
                            +   "####################################\n\n\n";
                String warning = "####################################\n" + "# " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n";
                warning += distance < mMaxDistance ? "# Elmetto Indossato!\n" : "# Elmetto NON Indossato!\n";
                warning += "# Distanza: " + String.valueOf(distance) + "\n####################################\n\n\n";
                Log.v("ELMETTO DISTANZA", "DISTANZA == " + String.valueOf(distance));
                Log.v("ELMETTO", distance < mMaxDistance ? "Elemetto Indossato" : "Elmetto NON Indossato");
                FileUtils.writeToFile(mContext, FILE_INFO, FILE_PATH, text, true);
                FileUtils.writeToFile(mContext, FILE_WARNINGS, FILE_PATH, warning, true);
            }
        }
        new java.io.File(mContext.getFilesDir() + FILE_PATH + FILE_RSSI).delete();
    }

    private double getDistance(int txPower, int rssi){
        return Math.pow(10, ((txPower - rssi) * 1.0) / 20);
    }

    private void checkDeviceFound(){
        if(mTxpower != null && mTxpower.size() == 0){
            String text =   "####################################\n"
                        +   "# " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n"
                        +   "# Elmetto NON Trovato!\n"
                        +   "####################################\n\n\n";
            FileUtils.writeToFile(this, FILE_WARNINGS, FILE_PATH, text, true);
            Log.v("ELMETTO NON TROVATO", "ELMETTO NON TROVATO");
        }
    }
    /* END Private Methods */

}
