package galiazat.btprivatetransferlibrary.connected.callbacks;

import galiazat.btprivatetransferlibrary.model.TransferModel;

/**
 * Created by Azat on 13.11.17.
 */

public interface ConnectedCallback {
    void onFileAccepted(TransferModel model);
    void onFileDownloadProgressChanged(TransferModel model, long downloaded, long total);
    void onFailure(Throwable t);

    String getDownloadPath();
}
