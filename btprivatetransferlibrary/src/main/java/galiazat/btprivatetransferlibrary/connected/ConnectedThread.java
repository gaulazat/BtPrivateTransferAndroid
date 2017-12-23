package galiazat.btprivatetransferlibrary.connected;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import galiazat.btprivatetransferlibrary.connected.callbacks.ConnectedCallbacksWrapper;
import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransferlibrary.model.TransferModel;
import galiazat.btprivatetransferlibrary.send.threads.ConnectingThread;
import galiazat.btprivatetransferlibrary.util.ByteArrayUtil;

/**
 * Created by Azat on 12.11.17.
 */

public class ConnectedThread extends Thread {

    private static final String TAG = ConnectingThread.class.getSimpleName();

    private BtDevice mDevice;
    private BluetoothSocket mSocket;
    private ConnectedCallbacksWrapper mCallback;
    private Handler mHandler;
    private boolean isEnabled = true;

    public ConnectedThread(BtDevice mDevice, BluetoothSocket mSocket, ConnectedCallbacksWrapper mCallback) {
        this.mDevice = mDevice;
        this.mSocket = mSocket;
        this.mCallback = mCallback;
        mHandler = new Handler();
    }

    @Override
    public void run() {
        while (isEnabled) {
            try {
                Log.d("Azat", "start Input Stream");
                InputStream mIn = mSocket.getInputStream();
                byte[] res = new byte[Long.SIZE / Byte.SIZE];
                byte[] mBuffer = new byte[1024];
                int len = 0;
                while (len < Long.SIZE / Byte.SIZE) {
                    int cur = mIn.read(mBuffer, 0, Long.SIZE / Byte.SIZE - len);
                    if (cur >= 0) {
                        if (cur >0) {
                            copyBytes(res, mBuffer, len, cur);
                            len += cur;
                        }
                    } else {
                        Log.d(TAG, "Read received -1, breaking");
                        break;
                    }
                }
                long transferDataBytesCount = ByteArrayUtil.getLong(res);
                Log.d("Azat bytes1", transferDataBytesCount + "");
                TransferModel transfer = startReadTransferModel(mIn, transferDataBytesCount);
                startReadFile(transfer, mIn);
//                FileOutputStream outFile = null;
//                long bytesReceived = 0;
//                int fileSize = 6403179;
//                File fileN = null;
//                while (bytesReceived < fileSize) {  // I send fileSize as msg prior to this file transmit
//                    int mLen = mIn.read(mBuffer);
//                    android.util.Log.d("Azat", mLen + "");
//                    if (mLen > 0) {
//                        if (outFile == null) {
//                            File mainFolder = new File(App.MAIN_FOLDER_PATH);
//                            if (!mainFolder.exists()) {
//                                mainFolder.mkdirs();
//                            }
//                            final String fileName = "song";
//                            fileN = new File(mainFolder, fileName);
//                            if (fileN.exists()) {
//                                fileN.delete();
//                            }
//                            outFile = new FileOutputStream(fileN, true);
//                        }
//                        bytesReceived += mLen;
//                        outFile.write(mBuffer, 0, mLen);
//                    } else {
//                        Log.d(TAG, "Read received -1, breaking");
//                        break;
//                    }
//                }
//                if (outFile != null) {
//                    outFile.flush();
//                    outFile.close();
//                    android.util.Log.d("Azat", "success" + fileN.length() + "");
//                    mHandler.obtainMessage(Constants.MESSAGE_READ, fileSize, -1, "song".getBytes())
//                            .sendToTarget();
//                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                 e.printStackTrace();
                try {
                    mSocket.close();
                } catch (final IOException e1) {
                    e1.printStackTrace();
                    if (mHandler!=null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mCallback != null) {
                                    mCallback.onFailure(e1);
                                }
                            }
                        });
                    }
                }
                if (mHandler!=null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallback != null) {
                                mCallback.onFailure(e);
                            }
                        }
                    });
                }
                return;
            }
        }

    }

    private void startReadFile(final TransferModel transfer, InputStream mIn) throws IOException {
        try {
            byte[] mBuffer = new byte[1024];
            FileOutputStream outFile = null;
            long bytesReceived = 0;
            final long fileSize = transfer.getBytesCount();
            File fileN = null;
            while (bytesReceived < fileSize) {  // I send fileSize as msg prior to this file transmit
                int mLen = mIn.read(mBuffer);
                android.util.Log.d("Azat", bytesReceived + "");
                if (mLen > 0) {
                    if (outFile == null){
                        String path;
                        if (mCallback != null){
                            path = mCallback.getDownloadPath();
                            if (!TextUtils.isEmpty(path)){
                                File mainFolder = new File(path);
                                if (!mainFolder.exists()) {
                                    mainFolder.mkdirs();
                                }
                                fileN = new File(mainFolder, transfer.getDescription());
                                if (fileN.exists()){
                                    fileN.delete();
                                }
                                outFile = new FileOutputStream(fileN, true);
                                transfer.setPath(fileN.getPath());
                            }
                        }
                    }
                    if (fileN == null){
                        throw new IllegalStateException("empty download path");
                    }
                    bytesReceived += mLen;
                    final long t = bytesReceived;
                    if (mHandler != null){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mCallback != null){
                                    mCallback.onDownload(transfer, t, fileSize);
                                }
                            }
                        });
                    }
                    outFile.write(mBuffer, 0, mLen);
                } else {
                    Log.d(TAG, "Read received -1, breaking");
                    break;
                }
            }
            if (outFile != null) {
                outFile.flush();
                outFile.close();
                android.util.Log.d("Azat", "success" + fileN.length() + "");
                if (mHandler!= null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallback != null) {
                                mCallback.onFileAccess(transfer);
                            }
                        }
                    });
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private TransferModel startReadTransferModel(InputStream mIn, long transferDataBytesCount) {
        int bytesReceived = 0;
        byte[] mas = new byte[(int) transferDataBytesCount];
        try {
            while (bytesReceived < transferDataBytesCount){
                byte[] mBuffer = new byte[1024];
                int mLen = mIn.read(mBuffer, 0, (int) (transferDataBytesCount - bytesReceived));
                android.util.Log.d("Azat", mLen + "");
                if (mLen > 0) {
                    copyBytes(mas, mBuffer, bytesReceived, mLen);
                    bytesReceived += mLen;
                } else {
                    Log.d(TAG, "Read received -1, breaking");
                    break;
                }
            }
            return TransferModel.transformFromBytes(mas);
        } catch (final IOException e) {
            e.printStackTrace();
            if (mHandler!= null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.onFailure(e);
                        }
                    }
                });
            }
            return null;
        }
    }

    private static void copyBytes(byte[] mas, byte[] mBuffer, int bytesReceived, int mLen) {
        System.arraycopy(mBuffer, 0, mas, bytesReceived, mLen);
    }

    public void cancel(){
        try {
            isEnabled = false;
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroyThread(){
        cancel();
        if (mCallback!=null){
            mCallback.destroy();
        }
        mHandler = null;
    }

}
