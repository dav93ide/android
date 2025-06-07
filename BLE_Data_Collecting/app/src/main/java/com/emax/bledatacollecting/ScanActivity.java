package com.emax.bledatacollecting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.IBeacon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity {

    private static final String THE_MAC = "00:15:83:00:8F:CB";
    private static final int MAX_RSSI_READING = 50;
    // Files
    private static final String FILE_RSSI = "rssi";
    private static final String FILE_INFO = "info";
    private static final String FILE_RSSI_CONNECTED = "rssi_loop";
    private static final String FILE_INFO_CONNECTED = "info_loop";
    private static final String FILE_AVERAGE = "average";
    // Labels
    private static final String SCAN_ENDED = "Scansione terminata.";
    private static final String IS_SCANNING = "Scansione in corso...";
    private static final String READING_RSSI = "Lettura RSSI in corso...";

    @BindView(R.id.tv_scan_status)
    TextView mtvScanStatus;
    @BindView(R.id.et_dirname)
    EditText metDirname;
    @BindView(R.id.bttn_stop_scan)
    Button mbStopScan;
    @BindView(R.id.bttn_start_scan)
    Button mbStartScan;
    @BindView(R.id.bttn_get_rssi)
    Button mbGetRssi;

    // Bluetooth
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLeScanner;
    private ScanCallback mScanCallback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mBluetoothGattCallback;

    private boolean isScanning;
    private boolean mConnect;
    private int mTxPower;
    private int mCount;
    private String mDirName;
    private String mRssiFile;
    private String mInfoFile;

    /* START Override Lifecycle Methods */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initAll();
    }
    /* END Override Lifecycle Methods */

    /* START Actions Methods */
    @OnClick(R.id.bttn_stop_scan)
    public void onClickStopScan(View view){
        stopScan();
    }

    @OnClick(R.id.bttn_start_scan)
    public void onClickStartScan(View view){
        startScan();
    }

    @OnClick(R.id.bttn_get_rssi)
    public void onClickGetRssi(View view){
        if(isScanning){
            mConnect = true;
        } else {
            Toast.makeText(this, "E` necessario avviare la scansione...", Toast.LENGTH_SHORT).show();
        }
    }
    /* END Actions Methods */

    /* START Private Methods *//* START Init Methods */
    private void initAll(){
        mRssiFile = null;
        mInfoFile = null;
        isScanning = false;
        mConnect = false;
        initBluetoothAdapter();
        initBluetoothLeScanner();
        initBluetoothScanCallback();
        initBluetoothGattCallback();
    }

    private void initBluetoothAdapter(){
        final BluetoothManager bluetoothManager = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void initBluetoothLeScanner(){
        mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
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
                if(device.getAddress().equals(THE_MAC)){
                    List<ADStructure> structures = ADPayloadParser.getInstance().parse(result.getScanRecord().getBytes());
                    if(structures != null && structures.size() > 0){
                        for(ADStructure st : structures){
                            if(st instanceof IBeacon){
                                IBeacon beacon = (IBeacon) st;
                                mTxPower = beacon.getPower();
                            }
                        }
                    }
                    String textRssi = String.valueOf(result.getRssi()) + "\n";
                    String textInfo =   "######################\n"
                                    +   "# Distanza: " + String.valueOf(getDistance(mTxPower, result.getRssi())) + "\n"
                                    +   "# RSSI: " + String.valueOf(result.getRssi()) + "\n"
                                    +   "######################\n\n";
                    FileUtils.writeToFile(mContext, FILE_RSSI, mDirName + "/", textRssi, true);
                    FileUtils.writeToFile(mContext, FILE_INFO, mDirName + "/", textInfo, true);

                    if(mConnect) {
                        runOnUiThread(getRunnableSetTextScanStatus(READING_RSSI));
                        device.connectGatt(mContext, true, mBluetoothGattCallback);
                        mConnect = false;
                    }
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

    private void initBluetoothGattCallback(){
        final Context mContext = this;
        mBluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(status == BluetoothGatt.GATT_SUCCESS){
                    if(newState == BluetoothGatt.STATE_CONNECTED){
                        if(mBluetoothGatt == null){
                            mBluetoothGatt = gatt;
                        }
                        gatt.readRemoteRssi();
                    }
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                onReadRSSI(gatt, rssi, status);
            }
        };
    }
    /* END Init Methods */

    /* START Synchronized Methods */
    synchronized private void onReadRSSI(BluetoothGatt gatt, int rssi, int status){
        if(status == BluetoothGatt.GATT_SUCCESS){
            if(mRssiFile == null){
                mRssiFile = FILE_RSSI_CONNECTED + "_" + String.valueOf(Math.random() * 100);
            }
            if(mInfoFile == null){
                mInfoFile = FILE_INFO_CONNECTED + "_" + String.valueOf(Math.random() * 100);
            }
            incrementNMis();
            double distance = getDistance(mTxPower, rssi);
            String textRssi = String.valueOf(rssi) + "\n";
            String textInfo =   "####################################\n"
                            +   "# Distance: " + String.valueOf(distance) + "\n"
                            +   "# RSSI: " + String.valueOf(rssi) + "\n"
                            +   "# TxPower: " + String.valueOf(mTxPower) + "\n"
                            +   "# Count: " + String.valueOf(mCount) + "\n"
                            +   "###################################\n\n\n";
            FileUtils.writeToFile(this, mRssiFile, mDirName + "/", textRssi, true);
            FileUtils.writeToFile(this, mInfoFile, mDirName + "/", textInfo, true);
            if(checkNMis()){
                gatt.readRemoteRssi();
            } else {
                gatt.disconnect();
                doTheMagic();
                mRssiFile = null;
                mInfoFile = null;
                runOnUiThread(getRunnableSetTextScanStatus(IS_SCANNING));
            }
        }
    }

    private void  doTheMagic(){
        String fileText = FileUtils.readFromFile(this, mRssiFile, mRssiFile, true);
        if(fileText != null && !fileText.isEmpty()){
            String[] splitted = fileText.split("\n");
            if(splitted != null && splitted.length > 0){
                Integer tot = 0;
                for(String txt : splitted){
                    Integer value = Integer.parseInt(txt);
                    tot += value;
                }
                Integer average = tot / splitted.length;
                double distance = getDistance(mTxPower, average);
                String text =   "####################################\n"
                    +   "# Media RSSI: " + String.valueOf(average) + "\n"
                    +   "# Distanza: " + String.valueOf(distance) + "\n"
                    +   "####################################\n\n\n";
                FileUtils.writeToFile(this, FILE_AVERAGE, mDirName + "/", text, true);
            }
        }
    }

    synchronized private void incrementNMis(){
        mCount++;
    }

    synchronized private boolean checkNMis(){
        return mCount <= MAX_RSSI_READING;
    }
    /* END Synchronized Methods */

    /* START Scan Methods */
    private void startScan(){
        mCount = 0;
        if(!metDirname.getText().toString().isEmpty()) {
            mDirName = metDirname.getText().toString();
            if(!isScanning) {
                isScanning = true;
                mLeScanner.startScan(mScanCallback);
                mtvScanStatus.setText(IS_SCANNING);
            }
        } else {
            Toast.makeText(this, "E` necessario inserire un nome per la directory...", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopScan(){
        if(isScanning) {
            isScanning = false;
            mConnect = false;
            if(mBluetoothGatt != null){
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            }
            mLeScanner.stopScan(mScanCallback);
            mtvScanStatus.setText(SCAN_ENDED);
        }
    }
    /* END Scan Methods */

    /* START Runnables Methods */
    private Runnable getRunnableSetTextScanStatus(final String text){
        return new Runnable() {
            @Override
            public void run() {
                mtvScanStatus.setText(text);
            }
        };
    }
    /* END Runnables Methods */

    private double getDistance(int txPower, int rssi){
        return Math.pow(10, ((txPower - rssi) * 1.0) / 20);
    }

    /* END Private Methods */

}
