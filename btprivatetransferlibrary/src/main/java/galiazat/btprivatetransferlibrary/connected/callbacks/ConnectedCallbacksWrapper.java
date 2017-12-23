package galiazat.btprivatetransferlibrary.connected.callbacks;

import galiazat.btprivatetransferlibrary.model.TransferModel;

/**
 * Created by Azat on 13.11.17.
 */

public class ConnectedCallbacksWrapper {

    private ConnectedCallback mConnectedCallback;
    private UtilConnectedCallback mUtilCallback;

    public ConnectedCallbacksWrapper(ConnectedCallback mConnectedCallback, UtilConnectedCallback mUtilCallback) {
        this.mConnectedCallback = mConnectedCallback;
        this.mUtilCallback = mUtilCallback;
    }

    public void onFileAccess(TransferModel model){
        if (mConnectedCallback != null){
            mConnectedCallback.onFileAccepted(model);
        }
    }

    public void onDownload(TransferModel model, long downloaded, long total){
        if (mConnectedCallback != null){
            mConnectedCallback.onFileDownloadProgressChanged(model, downloaded, total);
        }
    }

    public void onFailure(Throwable t){
        if (mUtilCallback!= null){
            mUtilCallback.onFailure();
        }
        if (mConnectedCallback != null){
            mConnectedCallback.onFailure(t);
        }
    }


    public void destroy() {
        mUtilCallback = null;
        mConnectedCallback = null;
    }

    public String getDownloadPath() {
        if (mConnectedCallback != null){
            return mConnectedCallback.getDownloadPath();
        }
        return null;
    }
}
