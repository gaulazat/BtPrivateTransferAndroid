package galiazat.btprivatetransferlibrary.accept.callbacks;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 13.11.17.
 */

public class AcceptCallbackWrapper {

    private AcceptCallback mAcceptCallback;
    private UtilAcceptCallback mUtilAcceptCallback;

    public AcceptCallbackWrapper(AcceptCallback mAcceptCallback, UtilAcceptCallback mAcceptCallbackWrapper) {
        this.mAcceptCallback = mAcceptCallback;
        this.mUtilAcceptCallback = mAcceptCallbackWrapper;
    }

    public void connected(BluetoothSocket socket, BtDevice device, String socketType){
        if (mAcceptCallback!= null){
            mAcceptCallback.onConnected(socket, device, socketType);
        }
        if (mUtilAcceptCallback != null){
            mUtilAcceptCallback.onConnected(socket, device, socketType);
        }
    }

    public void destroy(){
        mAcceptCallback = null;
        mUtilAcceptCallback = null;
    }

}
