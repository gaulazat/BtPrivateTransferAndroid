package galiazat.btprivatetransferlibrary.accept.callbacks;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 13.11.17.
 */

public interface AcceptCallback {
    void onConnected(BluetoothSocket socket, BtDevice device, String socketType);
    void onFailure(Exception e);
}
