package galiazat.btprivatetransferlibrary.model;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Azat on 12.11.17.
 */

public class BtDevice {

    private BluetoothDevice mDevice;
    private int id;
    private boolean isPaired;

    public BtDevice(BluetoothDevice mDevice, int id) {
        this.mDevice = mDevice;
        this.id = id;
    }

    public BtDevice(BluetoothDevice mDevice, int id, boolean isPaired) {
        this.mDevice = mDevice;
        this.id = id;
        this.isPaired = isPaired;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getId() {
        return id;
    }

    public boolean isPaired() {
        return isPaired;
    }
}
