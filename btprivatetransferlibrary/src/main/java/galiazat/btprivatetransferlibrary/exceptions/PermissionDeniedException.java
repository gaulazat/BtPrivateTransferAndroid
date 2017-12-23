package galiazat.btprivatetransferlibrary.exceptions;

/**
 * Created by Azat on 13.11.17.
 */

public class PermissionDeniedException extends Exception{

    public PermissionDeniedException(String permission) {
        super("check runtime permission " + permission);
    }
}
