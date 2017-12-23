package galiazat.btprivatetransfersample.models;

import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItem;

/**
 * Created by Azat on 27.11.17.
 */

public class EventDownloadProgress {

    NetDownloadedItem item;
    int progress;

    public EventDownloadProgress(NetDownloadedItem item, int id) {
        this.item = item;
        this.progress = id;
    }

    public NetDownloadedItem getItem() {
        return item;
    }

    public int getProgress() {
        return progress;
    }
}
