package galiazat.btprivatetransferlibrary.util;

import java.nio.ByteBuffer;

/**
 * Created by Azat on 26.11.17.
 */

public class ByteArrayUtil {

    public static final int LONG_SIZE = Long.SIZE / Byte.SIZE;

    public static long getLong(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.allocate(LONG_SIZE);
        byte[] bytes1 = new byte[LONG_SIZE];
        System.arraycopy(bytes, 0, bytes1, 0, bytes1.length);
        byteBuffer.put(bytes1);
        byteBuffer.flip();
        return byteBuffer.getLong();
    }
    public static long getLong(byte[] bytes, int offset){
        ByteBuffer byteBuffer = ByteBuffer.allocate(LONG_SIZE);
        byte[] bytes1 = new byte[LONG_SIZE];
        System.arraycopy(bytes, offset, bytes1, 0, bytes1.length);
        byteBuffer.put(bytes1);
        byteBuffer.flip();
        return byteBuffer.getLong();
    }
}
