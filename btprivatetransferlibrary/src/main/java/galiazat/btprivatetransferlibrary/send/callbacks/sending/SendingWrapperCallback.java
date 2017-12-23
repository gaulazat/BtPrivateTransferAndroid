package galiazat.btprivatetransferlibrary.send.callbacks.sending;

import galiazat.btprivatetransferlibrary.model.TransferModel;

/**
 * Created by Azat on 13.11.17.
 */

public class SendingWrapperCallback {

    private UtilSendingCallback mUtilCallback;
    private SendingCallback mSendingCallback;

    public SendingWrapperCallback(UtilSendingCallback mUtilCallback, SendingCallback mSendingCallback) {
        this.mUtilCallback = mUtilCallback;
        this.mSendingCallback = mSendingCallback;
    }

    public void sendingFailure(Throwable t){
        if (mUtilCallback!=null){
            mUtilCallback.onSendingFailure(t);
        }
        if (mSendingCallback!=null){
            mSendingCallback.onSendingFailure(t);
        }
    }

    public void sendingSuccess(String mPath, TransferModel mModel) {
        if (mUtilCallback!=null){
            mUtilCallback.onSendingSuccess(mModel);
        }
        if (mSendingCallback!=null){
            mSendingCallback.onSendingSuccess(mModel);
        }
    }
}
