package galiazat.btprivatetransferlibrary.send.callbacks.connecting;

import android.bluetooth.BluetoothSocket;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 13.11.17.
 */

public interface UtilConnectingCallback {

    void connectingSuccess(BtDevice device, BluetoothSocket socket, String socketType);
    void connectingFailed(BtDevice device, Throwable throwable);

}
