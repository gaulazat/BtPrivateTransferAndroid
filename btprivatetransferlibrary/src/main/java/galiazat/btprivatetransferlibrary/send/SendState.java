package galiazat.btprivatetransferlibrary.send;

/**
 * Created by Azat on 13.11.17.
 */

public interface SendState {

    int IDLE = 0;
    int SENDING = 1;
    int CONNECTING = 2;
    int FAILURE = 3;
    int CONNECTED = 4;
    int LISTENING = 6;

}
