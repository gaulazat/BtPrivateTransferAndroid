package galiazat.btprivatetransfersample;

import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItem;

/**
 * Created by Azat on 27.11.17.
 */

public interface DownloadCallback {

    void onDownloadStarted(NetDownloadedItem item);
    void onDownloadEnded(NetDownloadedItem item);

}
