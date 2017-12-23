package galiazat.btprivatetransferlibrary.send.callbacks.sending;

import galiazat.btprivatetransferlibrary.model.TransferModel;

/**
 * Created by Azat on 13.11.17.
 */

public interface SendingCallback {

    void onSendingFailure(Throwable t);
    void onSendingSuccess(TransferModel model);

}
