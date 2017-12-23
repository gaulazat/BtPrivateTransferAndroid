package galiazat.btprivatetransferlibrary.send.callbacks.connecting;

import android.bluetooth.BluetoothSocket;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 13.11.17.
 */

public class ConnectingCallbackWrapper {

    private ConnectingCallback mConnectingCallback;
    private UtilConnectingCallback mUtilConnectingCallback;
    private boolean isSuccess = false;

    public ConnectingCallbackWrapper(ConnectingCallback mConnectingCallback, UtilConnectingCallback mUtilConnectingCallback) {
        this.mConnectingCallback = mConnectingCallback;
        this.mUtilConnectingCallback = mUtilConnectingCallback;
    }

    public void onSuccess(BtDevice btDevice, BluetoothSocket socket, String socketType){
        isSuccess = true;
        if (mUtilConnectingCallback != null){
            mUtilConnectingCallback.connectingSuccess(btDevice, socket, socketType);
        }
        if (mConnectingCallback != null){
            mConnectingCallback.onConnectingSuccess(btDevice);
        }
    }

    public void onFailure(BtDevice btDevice, Throwable throwable){
        if (mConnectingCallback != null){
            mConnectingCallback.onConnectingFailure(btDevice, throwable);
        }
        if (mUtilConnectingCallback != null){
            mUtilConnectingCallback.connectingFailed(btDevice, throwable);
        }
    }

    public void destroy(){
        mConnectingCallback = null;
        mUtilConnectingCallback = null;
    }

}
