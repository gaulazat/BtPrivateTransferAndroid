package galiazat.btprivatetransferlibrary.model;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import galiazat.btprivatetransferlibrary.util.ArrayConcater;
import galiazat.btprivatetransferlibrary.util.ByteArrayUtil;

/**
 * Created by Azat on 07.11.17.
 */

public class TransferModel {

    private long mBytesCount;
    private String mDescription;
    private String mPath;

    public TransferModel(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getBytesCount() {
        return mBytesCount;
    }

    public void setBytesCount(long mBytesCount) {
        this.mBytesCount = mBytesCount;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public byte[] getBytes(){
        byte[] sendingBytes = ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(mBytesCount).array();
        byte[] descr = mDescription.getBytes();
        long a = descr.length;
        byte[] sendingBytes1 = ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(a).array();
        sendingBytes = ArrayConcater.concatMultipleByteArrays(sendingBytes, sendingBytes1);
        sendingBytes = ArrayConcater.concatMultipleByteArrays(sendingBytes, descr);
        sendingBytes = ArrayConcater.concatMultipleByteArrays(sendingBytes, mPath.getBytes());
        return sendingBytes;
    }

    public static TransferModel transformFromBytes(byte[] mas) {
        long bytesCount = ByteArrayUtil.getLong(mas);
        int descriptionSize = (int) ByteArrayUtil.getLong(mas, ByteArrayUtil.LONG_SIZE);
        String descr = null;
        String mPath = null;
        descr = new String(mas, 2 * ByteArrayUtil.LONG_SIZE,
                descriptionSize,  Charset.defaultCharset());
        mPath = new String(mas, 2 * ByteArrayUtil.LONG_SIZE + descriptionSize,
                mas.length - (2 * ByteArrayUtil.LONG_SIZE + descriptionSize),  Charset.defaultCharset());
        TransferModel tranferModel = new TransferModel(descr);
        tranferModel.setBytesCount(bytesCount);
        tranferModel.setPath(mPath);
        return tranferModel;
    }

}
