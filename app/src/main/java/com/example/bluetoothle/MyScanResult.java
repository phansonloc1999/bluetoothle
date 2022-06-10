package com.example.bluetoothle;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.io.Serializable;

public class MyScanResult implements Serializable {
    private String address;
    private String rssi;
    private String deviceName;
    private String uuid;
    private BluetoothDevice bluetoothDevice;
    private String txPower;
    private String major;
    private String minor;

    public MyScanResult(String address, String rssi, String deviceName, String uuid, BluetoothDevice bluetoothDevice, String txPower, String major, String minor) {
        this.address = address;
        this.rssi = rssi;
        this.deviceName = deviceName;
        this.uuid = uuid;
        this.bluetoothDevice = bluetoothDevice;
        this.txPower = txPower;
        this.major = major;
        this.minor = minor;
    }

    @Override
    public String toString() {
        String result = "";
        result += "Address: " + this.address + " RSSI: " + this.rssi + "\nDevice name: " + this.deviceName + " TxPower: " + this.txPower
                + "\n";
        if (deviceName.equals("iBeacon")) {
            result += " UUID: " + this.uuid + " Major: " + this.major + " Minor: " + this.minor;
        }
        return result;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
