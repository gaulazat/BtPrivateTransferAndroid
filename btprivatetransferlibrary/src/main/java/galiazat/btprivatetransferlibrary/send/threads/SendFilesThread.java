package galiazat.btprivatetransferlibrary.send.threads;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import galiazat.btprivatetransferlibrary.model.TransferModel;
import galiazat.btprivatetransferlibrary.send.callbacks.sending.SendingWrapperCallback;

/**
 * Created by Azat on 13.11.17.
 */

public class SendFilesThread extends Thread {

    private TransferModel mModel;
    private BluetoothSocket mBluetoothSocket;
    private Handler mHandler;
    private SendingWrapperCallback mCallback;

    public SendFilesThread(TransferModel mModel, BluetoothSocket mBluetoothSocket, SendingWrapperCallback callback) {
        this.mModel = mModel;
        this.mBluetoothSocket = mBluetoothSocket;
        mCallback = callback;
        mHandler = new Handler();
    }

    @Override
    public void run() {
        write();
    }

    private void write() {
        byte[] modelBytes = mModel.getBytes();
        OutputStream outputStream = null;
        try {
            outputStream = mBluetoothSocket.getOutputStream();
            Log.d("Azat", modelBytes.length + "");
            byte[] sendingBytes = ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(modelBytes.length).array();
            outputStream.write(sendingBytes);
//            outputStream.flush();
            outputStream.write(modelBytes);
//            outputStream.flush();
            Log.d("Azat", "sendingFiles");
            sendFile(outputStream);
        } catch (final IOException e) {
            e.printStackTrace();
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null){
                        mCallback.sendingFailure(e);
                    }
                }
            });
        }

//        final File fileN = new File(mPath);
//
//        byte[] mBuffer = new byte[1024 * 1024];
//        try {
//            OutputStream mOut = mBluetoothSocket.getOutputStream();
//            InputStream inFile = new FileInputStream(fileN);
//            while((mLen = inFile.read(mBuffer, 0, mBuffer.length)) > 0){
//                mOut.write(mBuffer, 0, mLen);
//            }
//            inFile.close();
//            mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, (fileN.length() + "").getBytes())
//                    .sendToTarget();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void sendFile(OutputStream mOut) {
        final File fileN = new File(mModel.getPath());
        android.util.Log.d("AZAT4", fileN.length() + "");
        int mLen;
            /*Transmit*/
        byte[] mBuffer = new byte[1024 * 1024];
        try {
            InputStream inFile = new FileInputStream(fileN);
            while((mLen = inFile.read(mBuffer, 0, mBuffer.length)) > 0){
                Log.d("Azat", mLen + "");
                mOut.write(mBuffer, 0, mLen);
            }
            inFile.close();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null){
                        mCallback.sendingSuccess(mModel.getPath(), mModel);
                    }
                }
            });
        } catch (IOException e) {
            try {
                mOut.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
