package galiazat.btprivatetransferlibrary.util;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Azat on 26.11.17.
 */

public class ArrayConcater {

    public static byte[] concatMultipleByteArrays(byte[]... arrays)
    {
        int length = 0;
        for (byte[] array : arrays)
        {
            length += array.length;
        }
        byte[] result = new byte[length];
        int offset=0;
        for (byte[] bytes : arrays){
            System.arraycopy(bytes, 0, result, offset, bytes.length);
            offset+=bytes.length;
        }
        return result;
    }
}
