package com.example.bluetoothle;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.io.Serializable;

public class MyScanResult implements Serializable {
    private String address;
    private String rssi;
    private String deviceName;
    private ParcelUuid[] parcelUuids;
    private BluetoothDevice bluetoothDevice;
    private String txPower;

    public MyScanResult(String address, String rssi, String deviceName, ParcelUuid[] parcelUuids, BluetoothDevice bluetoothDevice, String txPower) {
        this.address = address;
        this.rssi = rssi;
        this.deviceName = deviceName;
        this.parcelUuids = parcelUuids;
        this.bluetoothDevice = bluetoothDevice;
        this.txPower = txPower;
    }

    @Override
    public String toString() {
        return "Address: " + this.address + " RSSI: " + this.rssi + "\nDevice name: " + this.deviceName + " TxPower: " + this.txPower  + "\n";
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
}
