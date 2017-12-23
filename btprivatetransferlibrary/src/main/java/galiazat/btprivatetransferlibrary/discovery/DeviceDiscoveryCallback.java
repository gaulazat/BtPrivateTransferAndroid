package galiazat.btprivatetransferlibrary.discovery;

import android.bluetooth.BluetoothDevice;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 07.11.17.
 */

public interface DeviceDiscoveryCallback {

    void deviceFounded(BtDevice device);
    void discoveryFinished(Throwable t);

}
