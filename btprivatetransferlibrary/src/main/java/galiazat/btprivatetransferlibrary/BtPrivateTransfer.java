package galiazat.btprivatetransferlibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import galiazat.btprivatetransferlibrary.accept.AcceptThread;
import galiazat.btprivatetransferlibrary.accept.callbacks.AcceptCallback;
import galiazat.btprivatetransferlibrary.accept.callbacks.AcceptCallbackWrapper;
import galiazat.btprivatetransferlibrary.accept.callbacks.UtilAcceptCallback;
import galiazat.btprivatetransferlibrary.btEnabled.BtStateChangedCallback;
import galiazat.btprivatetransferlibrary.btEnabled.BtStateChangedReceiver;
import galiazat.btprivatetransferlibrary.connected.ConnectedThread;
import galiazat.btprivatetransferlibrary.connected.callbacks.ConnectedCallback;
import galiazat.btprivatetransferlibrary.connected.callbacks.ConnectedCallbacksWrapper;
import galiazat.btprivatetransferlibrary.connected.callbacks.UtilConnectedCallback;
import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransferlibrary.model.TransferModel;
import galiazat.btprivatetransferlibrary.send.SendState;
import galiazat.btprivatetransferlibrary.send.callbacks.connecting.ConnectingCallback;
import galiazat.btprivatetransferlibrary.send.callbacks.connecting.ConnectingCallbackWrapper;
import galiazat.btprivatetransferlibrary.send.callbacks.connecting.UtilConnectingCallback;
import galiazat.btprivatetransferlibrary.send.callbacks.sending.SendingCallback;
import galiazat.btprivatetransferlibrary.send.callbacks.sending.SendingWrapperCallback;
import galiazat.btprivatetransferlibrary.send.callbacks.sending.UtilSendingCallback;
import galiazat.btprivatetransferlibrary.send.threads.ConnectingThread;
import galiazat.btprivatetransferlibrary.send.threads.SendFilesThread;
import galiazat.btprivatetransferlibrary.util.StaticIdGenerator;

/**
 * Created by Azat on 12.11.17.
 */

public class BtPrivateTransfer implements SendState, BtStateChangedCallback, UtilSendingCallback {

    protected BtPrivateTransfer(){

    }

    private String mSocketType;
    private BtDevice mBtDevice;
    private ConnectingCallback mConnectingCallbacks;
    private String mAppSecureString;
    private UUID uuid;
    private int mState = SendState.IDLE;
    private TransferModel mSendingFile;
    private ConnectingThread mConnectingThread;
    private AcceptThread mAcceptThread;
    private AcceptCallback mAcceptCallback;
    private ConnectedCallback mConnectedCallback;
    private SendingCallback mSendingCallback;
    private ConnectedThread mConnectedThread;
    private BtStateChangedReceiver mStateChangedReceiver = new BtStateChangedReceiver();
    private SendFilesThread mSendFilesThread;
    private BluetoothSocket mSocket;

    public void setConnectingCallbacks(ConnectingCallback mConnectinhCallbacks) {
        this.mConnectingCallbacks = mConnectinhCallbacks;
    }

    public void setDeviceSecureString(String mDeviceSecureString) {
        this.mAppSecureString = mDeviceSecureString;
        uuid = UUID.fromString(mAppSecureString);
    }

    public void setAcceptCallback(AcceptCallback mAcceptCallback) {
        this.mAcceptCallback = mAcceptCallback;
    }

    public void setConnectedCallback(ConnectedCallback mConnectedCallback) {
        this.mConnectedCallback = mConnectedCallback;
    }

    public void setSendingCallback(SendingCallback mSendingCallback) {
        this.mSendingCallback = mSendingCallback;
    }

    public void sendFiles(TransferModel fileMap){
        if (mState == CONNECTED) {
            mSendingFile = fileMap;
            sendFiles();
        } else {
            throw new IllegalStateException("Not connected to bluetooth device");
        }
    }

    public void startListenBtSocket(final ContextWrapper contextWrapper){
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            mStateChangedReceiver.addCallback(new BtStateChangedCallback() {
                @Override
                public void onEnabled() {
                    startListenBtSocket(contextWrapper);
                    contextWrapper.unregisterReceiver(mStateChangedReceiver);
                    mStateChangedReceiver.destroy();
                }

                @Override
                public void onDisabled() {

                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            contextWrapper.registerReceiver(mStateChangedReceiver, filter);
            BluetoothAdapter.getDefaultAdapter().enable();
            return;
        }
        stopAcceptThread();
        stopConnectedThread();
        cancelConnecting();
        if (mState == IDLE || mState == FAILURE){
            mState = LISTENING;
            mAcceptThread = new AcceptThread(false, uuid, new AcceptCallbackWrapper(mAcceptCallback, new UtilAcceptCallback() {
                @Override
                public void onConnected(BluetoothSocket socket, BtDevice device, String socketType) {
                    Log.d("Azat", "connected");
                    mBtDevice = device;
                    stopAcceptThread();
                    connected(mBtDevice, socket, socketType);
                }

                @Override
                public void onFailure(Exception e) {

                }
            }));
            mAcceptThread.start();
        }
    }

    public void connect(BtDevice device){
        stopAcceptThread();
        stopConnectedThread();
        mState = SendState.CONNECTING;
        mBtDevice = device;
        mConnectingThread = new ConnectingThread(mBtDevice, false, uuid,
                new ConnectingCallbackWrapper(mConnectingCallbacks, new UtilConnectingCallback() {
                    @Override
                    public void connectingSuccess(BtDevice device, BluetoothSocket socket, String socketType) {
                        mSocket = socket;
                        Log.d("Azat", "connected");
                        connected(device, socket, socketType);
                        mConnectingCallbacks.onConnectingSuccess(device);
                        if (mConnectingThread != null) {
                            mConnectingThread.destroyThread();
                            mConnectingThread = null;
                        }
                    }

                    @Override
                    public void connectingFailed(BtDevice device, Throwable throwable) {
                        mState = SendState.FAILURE;
                        mConnectingThread.destroyThread();
                        mConnectingThread = null;
                    }
                }));
        mConnectingThread.start();
    }

    public void connected(BtDevice device, BluetoothSocket socket, String socketType){
        mState = SendState.CONNECTED;
        stopAcceptThread();
        mBtDevice = device;
        mSocket = socket;
        mSocketType = socketType;
        mConnectedThread = new ConnectedThread(device, socket, new ConnectedCallbacksWrapper(mConnectedCallback, new UtilConnectedCallback() {
            @Override
            public void onFailure() {
                stopConnectedThread();
            }
        }));
        mConnectedThread.start();
    }

    private void stopAcceptThread(){
        if (mAcceptThread!= null){
            mAcceptThread.destroyThread();
            mAcceptThread = null;
        }
        if (mState == LISTENING){
            mState = IDLE;
        }
    }

    private void stopConnectedThread(){
        if (mConnectedThread!= null){
            mConnectedThread.cancel();
            mConnectedThread.destroyThread();
            mConnectedThread = null;
        }
        if (mState == CONNECTED){
            mState = IDLE;
        }
    }

    public void cancelConnecting(){
        mState = SendState.IDLE;
        if (mConnectingThread != null){
            mConnectingThread.cancel();
            mConnectingThread.destroyThread();
            mConnectingThread = null;
        }
    }

    private void sendFiles() {
        stopAcceptThread();
        File file = new File(mSendingFile.getPath());
        mSendingFile.setBytesCount(file.length());
        mState = SENDING;
        mSendFilesThread = new SendFilesThread(mSendingFile, mSocket,
                new SendingWrapperCallback(this, mSendingCallback));
        mSendFilesThread.start();
    }

    public void destroy(){
        cancelConnecting();
        stopConnectedThread();
        stopAcceptThread();
        if (mSocket!=null){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mConnectingCallbacks = null;
        mConnectedCallback = null;
        mAcceptCallback = null;
    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {

    }

    @Override
    public void onSendingFailure(Throwable t) {
        mState = IDLE;
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSendingSuccess(TransferModel model) {
        mState = CONNECTED;
        connected(mBtDevice, mSocket, mSocketType);
    }

    public BtDevice getCurrentBtDevice() {
        return mBtDevice;
    }

    public boolean isSending(){
        return mState == SENDING;
    }

    public boolean isConnected() {
        return mState == SENDING || mState == CONNECTED;
    }

    public static class Builder{

        private ConnectingCallback mConnectinhCallbacks;
        private AcceptCallback mAcceptCallback;
        private ConnectedCallback mConnectedCallback;
        private SendingCallback mSendingCallback;
        private String mAppSecureString;

        public Builder setSendingCallback(SendingCallback mSendingCallback) {
            this.mSendingCallback = mSendingCallback;
            return this;
        }

        public Builder setAcceptCallback(AcceptCallback mAcceptCallback) {
            this.mAcceptCallback = mAcceptCallback;
            return this;
        }

        public Builder setConnectedCallback(ConnectedCallback mConnectedCallback) {
            this.mConnectedCallback = mConnectedCallback;
            return this;
        }

        public Builder setConnectingCallbacks(ConnectingCallback mConnectinhCallbacks) {
            this.mConnectinhCallbacks = mConnectinhCallbacks;
            return this;
        }

        public Builder setAppSecureString(String mAppSecureString) {
            this.mAppSecureString = mAppSecureString;
            return this;
        }

        public BtPrivateTransfer build(){
            BtPrivateTransfer btPrivateTransfer = new BtPrivateTransfer();
            btPrivateTransfer.setConnectingCallbacks(mConnectinhCallbacks);
            btPrivateTransfer.setDeviceSecureString(mAppSecureString);
            btPrivateTransfer.setConnectedCallback(mConnectedCallback);
            btPrivateTransfer.setAcceptCallback(mAcceptCallback);
            btPrivateTransfer.setSendingCallback(mSendingCallback);
            return btPrivateTransfer;
        }
    }


}
