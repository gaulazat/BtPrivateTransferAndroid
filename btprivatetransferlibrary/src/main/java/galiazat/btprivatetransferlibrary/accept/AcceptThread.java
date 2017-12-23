package galiazat.btprivatetransferlibrary.accept;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import galiazat.btprivatetransferlibrary.accept.callbacks.AcceptCallbackWrapper;
import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransferlibrary.send.threads.ConnectingThread;
import galiazat.btprivatetransferlibrary.util.StaticIdGenerator;

/**
 * Created by Azat on 13.11.17.
 */

public class AcceptThread extends Thread{

    private static final String TAG = AcceptThread.class.getSimpleName();

    private static final String NAME_INSECURE = "BtPrivayeTransfer";
    // The local server socket
    private final BluetoothServerSocket mmServerSocket;
    private String mSocketType;
    private AcceptCallbackWrapper mCallback;
    private Handler handler;
    private boolean isEnabled = true;

    public AcceptThread(boolean secure, UUID uuid, AcceptCallbackWrapper callbackWrapper) {
        BluetoothServerSocket tmp = null;
        handler = new Handler();
        mCallback = callbackWrapper;
        mSocketType = secure ? "Secure" : "Insecure";

        // Create a new listening server socket
        try {
            if (secure) {
//                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
//                        MY_UUID_SECURE);
            } else {
                tmp = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(
                        NAME_INSECURE, uuid);
            }
        } catch (IOException e) {
            Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
        }
        mmServerSocket = tmp;
    }

    @Override
    public void run() {
        Log.d(TAG, "Socket Type: " + mSocketType +
                "BEGIN mAcceptThread" + this);

        BluetoothSocket socket = null;

        // Listen to the server socket if we're not connected
        while (isEnabled) {
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                break;
            }

            // If a connection was accepted
            if (socket != null) {
                final BluetoothSocket finalSocket = socket;
                final BluetoothSocket finalSocket1 = socket;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null){
                            BtDevice btDevice = new BtDevice(finalSocket1.getRemoteDevice(), StaticIdGenerator.getInstance().generateId(), true);
                            mCallback.connected(finalSocket, btDevice, mSocketType);
                        }
                    }
                });
                return;
            }
        }
        Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

    }

    public void cancel() {
        Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
        try {
            isEnabled = false;
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
        }
    }

    public void destroyThread(){
        if (mCallback != null){
            mCallback.destroy();
        }
        isEnabled = false;
        mCallback = null;
        handler = null;
    }
}
