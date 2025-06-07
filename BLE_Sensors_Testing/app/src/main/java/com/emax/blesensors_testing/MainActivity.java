package com.emax.blesensors_testing;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.IBeacon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 0x1;
    private static final long SCAN_PERIOD = 10000;
    private static final String[] mids = {"EMAX0001", "EMAX0002", "EMAX0003"};

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private ScanCallback mScanCallback;
    private boolean mScanning;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mGattCallback;
    private ArrayAdapter<String> mAdapter;
    private int mTxPower;

    @BindView(R.id.tv_elmetto)
    TextView mtvElmetto;
    @BindView(R.id.tv_guanti)
    TextView mtvGuanti;
    @BindView(R.id.tv_scarpe)
    TextView mtvScarpe;
    @BindView(R.id.tv_scanning)
    TextView mtvScanning;
    @BindView(R.id.bttn_scan)
    Button mButton;
    @BindView(R.id.lv_list)
    ListView mList;

    /* START Override Lifecycle Methods */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Non Supportato!", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent enableB = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableB, REQUEST_ENABLE_BT);
        }
        ButterKnife.bind(this);
        mHandler = new Handler();
        final Context context = this;
        mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    mBluetoothGatt = gatt;
                    final BluetoothDevice device = gatt.getDevice();
                    String intentAction;
                    final int newS = newState;
                    gatt.discoverServices();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (newS == BluetoothProfile.STATE_CONNECTED) {
                                switch(Arrays.asList(mids).indexOf(device.getName())){
                                    case 0:
                                        mtvElmetto.setText("Elmetto CONNESSO");
                                        break;
                                    case 1:
                                        mtvGuanti.setText("Guanti CONNESSO");
                                        break;
                                    case 2:
                                        mtvScarpe.setText("Scarpe CONNESSO");
                                        break;
                                }

                            } else if (newS == BluetoothProfile.STATE_DISCONNECTED) {
                                switch(Arrays.asList(mids).indexOf(device.getName())){
                                    case 0:
                                        mtvElmetto.setText("Elmetto SCONNESSO!");
                                        break;
                                    case 1:
                                        mtvGuanti.setText("Guanti SCONNESSO!");
                                        break;
                                    case 2:
                                        mtvScarpe.setText("Scarpe SCONNESSO!");
                                        break;
                                }
                            }
                        }
                    });
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        List<BluetoothGattService> list = gatt.getServices();
                        if(list != null && list.size() > 0){
                            for(BluetoothGattService service : list){
                                String uuid = service.getUuid().toString();
                                List<BluetoothGattCharacteristic> l = service.getCharacteristics();
                                if(l != null && l.size() > 0){
                                    for(BluetoothGattCharacteristic c : l){
                                        gatt.readCharacteristic(c);
                                    }
                                }
                            }
                        }
                    } else {
                        addTextToAdapter("onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                    }
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    final int frssi = rssi;
                    final int fstatus = status;
                    super.onReadRemoteRssi(gatt, rssi, status);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addTextToAdapter("RSSI: " + String.valueOf(frssi) + "\nStatus: " + String.valueOf(fstatus));
                            double val = ((mTxPower - frssi) * 1.0) / 20;
                            double distance = Math.pow(10, val);
                            addTextToAdapter("DISTANCE: " + String.valueOf(distance));
                        }
                    });
                }

            };
        mScanCallback = new ScanCallback(){

            @Override
            public void onScanFailed(int errorCode){
                addTextToAdapter("ERROR: onScanFailed() : ErrorCode = " + errorCode);
            }

            @Override
            public void onScanResult(int callbackType, ScanResult result){
                if (null != result){
                    BluetoothDevice device = result.getDevice();
                    String name = device.getName();
                    if (null != device && device.getName() != null && Arrays.asList(mids).contains(device.getName())){
                        addTextToAdapter("DEVICE FOUND: " + device.getName());
                        device.connectGatt(context, true, mGattCallback);
                        int rssi = result.getRssi();
                        addTextToAdapter("INITIAL RSSI = " + String.valueOf(rssi));
                        int pow = result.getTxPower();
                        addTextToAdapter("txPower From ScanResult == " + String.valueOf(pow));
                        int tx = result.getScanRecord().getTxPowerLevel();
                        addTextToAdapter("txPower Level == " + String.valueOf(tx));
                        byte[] b = result.getScanRecord().getBytes();
                        List<ADStructure> structures = ADPayloadParser.getInstance().parse(result.getScanRecord().getBytes());
                        if(structures != null && structures.size() > 0){
                            for(ADStructure st : structures){
                                if(st instanceof IBeacon){
                                    IBeacon beacon = (IBeacon) st;
                                    mTxPower = beacon.getPower();
                                    addTextToAdapter("txPower From Beacon == " + String.valueOf(mTxPower));
                                }
                            }
                        }
                        double val = ((mTxPower - rssi) * 1.0) / 20;
                        double distance = Math.pow(10, val);
                        addTextToAdapter("INITIAL DISTANCE = " + String.valueOf(distance));
                    } else
                        //addTextToAdapter("ERROR: onScanResult() : Cannot get BluetoothDevice !!!");
                        Log.e(TAG, "ERROR: onScanResult() : Cannot get BluetoothDevice !!!");
                } else {
                    //addTextToAdapter("ERROR: onScanResult() : Cannot get ScanResult !!!");
                    Log.e(TAG, "ERROR: onScanResult() : Cannot get ScanResult !!!");
                }
            }
        };
        ArrayList<String> items = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        mList.setAdapter(mAdapter);
    }
    /* END Override Lifecycle Methods */

    /* START Override Action Methods */
    @OnClick(R.id.bttn_scan)
    public void onClickScan(View view){
        scanLeDevice();
    }

    @OnClick(R.id.bttn_read)
    public void onReadRssi(View view){
        if(mBluetoothGatt != null && mBluetoothGatt.getDevice() != null) {
            mBluetoothGatt.readRemoteRssi();
        }
    }
    /* END Override Action Methods */

    /* START Private Methods */
    private void scanLeDevice() {
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if(mScanner != null && !mScanning){
            mScanner.startScan(mScanCallback);
            mScanning = true;
            mtvScanning.setText("Scansione in corso.");
        } else {
            mScanner.stopScan(mScanCallback);
            mScanning = false;
            if(mBluetoothGatt != null && mBluetoothGatt.getDevice() != null) {
                mBluetoothGatt.disconnect();
            }
            mtvScanning.setText("Scansione Terminata.");
        }
    }

    private void getDistance(){
        String address = null;
        if(mBluetoothGatt != null) {
            List<BluetoothDevice> devices = mBluetoothGatt.getConnectedDevices();
            if(devices != null && devices.size() > 0) {
                for (BluetoothDevice device : devices) {
                    if (device.getName().equals("EMAX0001")) {
                        address = device.getAddress();
                    }
                }
            }
        }
    }

    private void addTextToAdapter(String text){
        Date cal = Calendar.getInstance().getTime();
        DateFormat dF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = dF.format(cal);
        mAdapter.add("<[ " + date + "]>   " + text);
    }
    /* END Private Methods */

}
