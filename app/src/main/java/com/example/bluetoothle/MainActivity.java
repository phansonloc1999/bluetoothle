package com.example.bluetoothle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
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
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private static BluetoothLeScanner bluetoothLeScanner;

    private static boolean isScanning = false;

    private static Button quitBtn = null;
    private static Button scanBtn = null;
    private static ListView scanResultsListView = null;
    private static ArrayList<MyScanResult> scanResultsList = new ArrayList<>();
    private static ArrayList<String> scanResultDeviceAddresses = new ArrayList<>();

    private boolean checkDevicePermission(String permission) {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void showToastBluetoothGattConnected(final BluetoothGatt gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "Connected to BluetoothGatt!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private String bytesToHex(byte[] bytes) {
        String hex = "";
        for (int i = 0; i < bytes.length; i++) {
            hex += String.format("%02X", bytes[i]);
        }
        return hex;
    }

    // Reference: https://github.com/Jaosrikate/iBeacon-Android
    private boolean findBeaconPattern(byte[] scanRecord, String[] beaconInfo) {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //UUID detection
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            // major
            final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            // minor
            final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

            Log.i("findBeaconPattern", "UUID: " + uuid + "\\nmajor: " + major + "\\nminor" + minor);
            beaconInfo[0] = uuid;
            beaconInfo[1] = String.valueOf(major);
            beaconInfo[2] = String.valueOf(minor);
        }
        return patternFound;
    }

    private void initFields() {
        scanResultsListView = findViewById(R.id.scanResultsListView);
        scanResultsList = new ArrayList<>();
        final ArrayAdapter<MyScanResult> scanResultsArrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scanResultsList);
        scanResultsListView.setAdapter(scanResultsArrayAdapter);
        scanResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                MyScanResult item = (MyScanResult) adapterView.getItemAtPosition(i);
                item.getBluetoothDevice().connectGatt(adapterView.getContext(), false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                showToastBluetoothGattConnected(gatt);

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (gatt != null) gatt.discoverServices();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onServicesDiscovered (BluetoothGatt gatt, int status) {
                        List<BluetoothGattService> bluetoothGattServiceList = gatt.getServices();
                        if (!bluetoothGattServiceList.isEmpty())
                        {
                            for (BluetoothGattService service: bluetoothGattServiceList) {
                                List<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = service.getCharacteristics();
                                for (BluetoothGattCharacteristic characteristic: bluetoothGattCharacteristicList) {
                                    Log.i("printGattTable", "Service " + service.getUuid().toString()
                                            + " Characteristic: " + characteristic.toString());
                                }
                            }
                        }
                        else Log.i("printGattTable", "No services found!");
                    }
                });
            }
        });

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null) {
                    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                }
                else {
                    Log.i("App", "Bluetooth adapter not found!");
                    System.exit(1);
                }

                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, MY_REQUEST_CODE);
                    return;
                }

                if (bluetoothLeScanner != null) {
                    if (!isScanning) {
                        Log.i("App", "Running bluetooth low energy scan!");
                        isScanning = true;
                        scanBtn.setText(R.string.stop_scan);
                        scanResultsList.clear();
                        scanResultDeviceAddresses.clear();

                        bluetoothLeScanner.startScan(new ScanCallback() {
                            // Handle scan results
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void onScanResult(int callbackType, ScanResult result) {
                                BluetoothDevice device = result.getDevice();
                                String deviceAddress = device.getAddress();
                                String[] beaconInfo = new String[] { "", "", "" };

                                if (!scanResultDeviceAddresses.contains(deviceAddress)) {
                                    scanResultDeviceAddresses.add(deviceAddress);
                                    if (findBeaconPattern(result.getScanRecord().getBytes(), beaconInfo))
                                    {
                                        scanResultsArrayAdapter.add(new MyScanResult(deviceAddress, String.valueOf(result.getRssi()),
                                                device.getName() + "(iBeacon)", beaconInfo[0], device, String.valueOf(result.getTxPower()), beaconInfo[1], beaconInfo[2]));
                                    }
                                    else scanResultsList.add(new MyScanResult(deviceAddress, String.valueOf(result.getRssi()), device.getName() + "(Bluetooth)",
                                            "", device, String.valueOf(result.getTxPower()), "", ""));
                                    scanResultsArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    else {
                        bluetoothLeScanner.stopScan((ScanCallback) null);
                        scanBtn.setText(R.string.start_scan);
                        isScanning = false;
                    }
                }
                else Log.i("App", "Can not get bluetooth low energy scanner!");
            }
        });

        quitBtn = findViewById(R.id.quitBtn);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFields();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!(checkDevicePermission(Manifest.permission.BLUETOOTH) &&
                checkDevicePermission(Manifest.permission.BLUETOOTH_ADMIN) &&
                        checkDevicePermission(Manifest.permission.ACCESS_FINE_LOCATION))) {
            // You can directly ask for the permission.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Required permissions not granted. App exiting!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    System.exit(1);
                                }
                            });
                    alertDialog.show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
}
