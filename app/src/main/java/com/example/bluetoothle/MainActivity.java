package com.example.bluetoothle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private static BluetoothLeScanner bluetoothLeScanner;

    private static Button quitBtn = null;

    private boolean checkDevicePermission(String permission) {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(), permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void initFields() {
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

        if (checkDevicePermission(Manifest.permission.BLUETOOTH) &&
                checkDevicePermission(Manifest.permission.BLUETOOTH_ADMIN) &&
                        checkDevicePermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // You can use the API that requires the permission.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothLeScanner != null) {
                Log.i("INFO", "Running bluetooth low energy scan!");
                bluetoothLeScanner.startScan(new ScanCallback() {
                    public void onScanResult(int callbackType, ScanResult result) {
                        Log.i("INFO", "Remote device name: " + result.getDevice().getName());
                        Log.i("INFO", "Remote device UUID: " + result.getDevice().getUuids());
                        Log.i("INFO", "Remote device address: " + result.getDevice().getAddress());
                        Log.i("INFO", "Remote device bond state: " + result.getDevice().getBondState());
                    }
                });
            }
            else Log.i("INFO", "Can not get bluetooth low energy scanner!");

        } else {
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
