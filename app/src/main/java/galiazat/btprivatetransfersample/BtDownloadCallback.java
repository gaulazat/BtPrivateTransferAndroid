package galiazat.btprivatetransfersample;

import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItem;

/**
 * Created by Azat on 11.12.17.
 */

public interface BtDownloadCallback {

    void onDownloadStarted(BtDownloadedItem item);
    void onDownloadEnded(BtDownloadedItem item);


}
