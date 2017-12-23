package galiazat.btprivatetransfersample.models;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 27.11.17.
 */

public class BtDeviceSubject {

    private BtDevice device;
    private boolean isConnected;

    public BtDeviceSubject(BtDevice id, boolean isConnected) {
        this.device = id;
        this.isConnected = isConnected;
    }

    public BtDevice getDevice() {
        return device;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
