package com.example.bluetoothle;

import java.io.Serializable;

public class MyScanResult implements Serializable {
    private String deviceName;

    public MyScanResult(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return this.deviceName;
    }
}
