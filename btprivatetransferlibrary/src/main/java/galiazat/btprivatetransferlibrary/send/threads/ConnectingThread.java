package galiazat.btprivatetransferlibrary.send.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransferlibrary.send.callbacks.connecting.ConnectingCallbackWrapper;

/**
 * Created by Azat on 13.11.17.
 */

public class ConnectingThread extends Thread {

    private static final String TAG = ConnectingThread.class.getSimpleName();

    private final BluetoothSocket mmSocket;
    private final BtDevice mmDevice;
    private String mSocketType;
    private Handler mCallbackHandler;
    private ConnectingCallbackWrapper mConnectingCallbackWrapper;

    public ConnectingThread(BtDevice device, boolean secure, UUID UUID, ConnectingCallbackWrapper connectingCallbackWrapper) {
        mmDevice = device;
        this.mConnectingCallbackWrapper = connectingCallbackWrapper;
        this.mCallbackHandler = new Handler();
        BluetoothSocket tmp = null;
        mSocketType = secure ? "Secure" : "Insecure";

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            if (secure) {
                tmp = device.getDevice().createRfcommSocketToServiceRecord(
                        UUID);
            } else {
                tmp = device.getDevice().createInsecureRfcommSocketToServiceRecord(
                        UUID);
            }
        } catch (IOException e) {
            if (mConnectingCallbackWrapper != null){
                mConnectingCallbackWrapper.onFailure(mmDevice, e);
            }
            Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
        setName("ConnectThread" + mSocketType);

        // Always cancel discovery because it will slow down a connection
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            mmSocket.connect();
        } catch (final IOException e) {
            // Close the socket
            e.printStackTrace();
            try {
                mmSocket.close();
            } catch (final IOException e2) {
                Log.e(TAG, "unable to close() " + mSocketType +
                        " socket during connection failure", e2);
                mCallbackHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mConnectingCallbackWrapper != null){
                            mConnectingCallbackWrapper.onFailure(mmDevice, e2);
                        }
                    }
                });
            }
            if (mCallbackHandler != null) {
                mCallbackHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mConnectingCallbackWrapper != null) {
                            mConnectingCallbackWrapper.onFailure(mmDevice, e);
                        }
                    }
                });
            }
            return;
        }
        mCallbackHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConnectingCallbackWrapper != null){
                    mConnectingCallbackWrapper.onSuccess(mmDevice, mmSocket, mSocketType);
                }
            }
        });
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (final IOException e) {
            Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            mCallbackHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mConnectingCallbackWrapper != null){
                        mConnectingCallbackWrapper.onFailure(mmDevice, e);
                    }
                }
            });
        }
    }

    public void destroyThread() {
        if (mConnectingCallbackWrapper != null){
            mConnectingCallbackWrapper.destroy();
            mConnectingCallbackWrapper = null;
        }
        mCallbackHandler = null;
    }
}
