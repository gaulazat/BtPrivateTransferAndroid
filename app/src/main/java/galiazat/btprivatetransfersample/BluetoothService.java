package galiazat.btprivatetransfersample;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import galiazat.btprivatetransferlibrary.BtPrivateTransfer;
import galiazat.btprivatetransferlibrary.accept.callbacks.AcceptCallback;
import galiazat.btprivatetransferlibrary.connected.callbacks.ConnectedCallback;
import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransferlibrary.model.TransferModel;
import galiazat.btprivatetransferlibrary.send.callbacks.connecting.ConnectingCallback;
import galiazat.btprivatetransferlibrary.send.callbacks.sending.SendingCallback;
import galiazat.btprivatetransfersample.models.BtDeviceSubject;
import galiazat.btprivatetransfersample.models.BtDownloadEvent;
import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItem;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by Azat on 13.11.17.
 */

public class BluetoothService extends Service implements AcceptCallback, ConnectingCallback, ConnectedCallback, SendingCallback {

    private BtPrivateTransfer mBtTransfer;

    private static BluetoothService sService;

    private ReplaySubject<BtDeviceSubject> connectedSubject = ReplaySubject.createWithSize(100);
    private ReplaySubject<BtDownloadEvent> downloadProgressSubject = ReplaySubject.createWithSize(100);

    private List<TransferModel> sendingFiles = new ArrayList<>();

    private BtDownloadCallback mCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sService = this;
        mBtTransfer = new BtPrivateTransfer.Builder()
                .setAcceptCallback(this)
                .setAppSecureString("8ce255c0-200a-11e0-ac64-0800200c9a66")
                .setConnectedCallback(this)
                .setConnectingCallbacks(this)
                .setSendingCallback(this)
                .build();
        mBtTransfer.startListenBtSocket(this);
    }

    public static BluetoothService getSharedService() {
        return sService;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBtTransfer.destroy();
        mCallback = null;
    }

    @Override
    public void onFileAccepted(TransferModel model) {
        Toast.makeText(App.get(), "File saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(Throwable t) {
        mBtTransfer.startListenBtSocket(this);
    }

    @Override
    public String getDownloadPath() {
        return App.MAIN_FOLDER_PATH;
    }

    @Override
    public void onConnectingSuccess(BtDevice device) {
        startSendingFiles();
        connectedSubject.onNext(new BtDeviceSubject(device, true));
    }

    private void startSendingFiles(){
        if (sendingFiles.size() != 0){
            mBtTransfer.sendFiles(sendingFiles.get(0));
            sendingFiles.remove(0);
        }
    }

    @Override
    public void onConnectingFailure(BtDevice device, Throwable t) {
        Toast.makeText(App.get(), "connection failure", Toast.LENGTH_LONG).show();
        connectedSubject.onNext(new BtDeviceSubject(device, false));
    }

    @Override
    public void onConnected(BluetoothSocket socket, BtDevice device, String socketType) {
        Toast.makeText(App.get(), "connection success", Toast.LENGTH_LONG).show();
        connectedSubject.onNext(new BtDeviceSubject(device, true));
    }

    @Override
    public void onFailure(Exception e) {

    }

    public void connect(BtDevice device) {
        mBtTransfer.connect(device);
    }

    public void sendFile(TransferModel model) {
        sendingFiles.add(model);
        if (mBtTransfer.getCurrentBtDevice()!= null && mBtTransfer.isConnected()){
            if (!mBtTransfer.isSending()){
                startSendingFiles();
            }
        }
    }

    public boolean isConnected(){
        return mBtTransfer.getCurrentBtDevice()!= null && mBtTransfer.isConnected();
    }

    @Override
    public void onSendingFailure(Throwable t) {
        mBtTransfer.startListenBtSocket(this);
    }

    @Override
    public void onSendingSuccess(TransferModel model) {
        if (sendingFiles.size()!=0){
            onConnectingSuccess(null);
        }
    }

    @Override
    public void onFileDownloadProgressChanged(TransferModel model, long downloaded, long total) {
        BtDownloadedItem item = new BtDownloadedItem();
        item.name = model.getDescription();
        item.path = model.getPath();
        downloadProgressSubject.onNext(new BtDownloadEvent(item, (int) (downloaded * 100 / total)));
    }

    public ReplaySubject<BtDeviceSubject> getConnectedSubject() {
        return connectedSubject;
    }
    public ReplaySubject<BtDownloadEvent> getDownloadSubject() {
        return downloadProgressSubject;
    }

    public void setCallback(BtDownloadCallback mCallback) {
        this.mCallback = mCallback;
    }
}
