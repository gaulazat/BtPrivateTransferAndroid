package galiazat.btprivatetransferlibrary.send.callbacks.connecting;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 13.11.17.
 */

public interface ConnectingCallback {

    void onConnectingSuccess(BtDevice device);
    void onConnectingFailure(BtDevice device, Throwable t);

}
