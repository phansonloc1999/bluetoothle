package com.example.bluetoothle;

import android.os.ParcelUuid;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class MyScanResult implements Serializable {
    private String address;
    private String rssi;
    private String deviceName;
    private ParcelUuid[] parcelUuids;

    public MyScanResult(String address, String rssi, String deviceName, ParcelUuid[] parcelUuids) {
        this.address = address;
        this.rssi = rssi;
        this.deviceName = deviceName;
        this.parcelUuids = parcelUuids;
    }

    @Override
    public String toString() {
        return "Address: " + this.address + " RSSI: " + this.rssi + "\nDevice name: " + this.deviceName;
    }
}
