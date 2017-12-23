package galiazat.btprivatetransfersample.models;

import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItem;

/**
 * Created by Azat on 11.12.17.
 */

public class BtDownloadEvent {

    BtDownloadedItem item;
    int progress;

    public BtDownloadEvent(BtDownloadedItem item, int progress) {
        this.item = item;
        this.progress = progress;
    }

    public BtDownloadedItem getItem() {
        return item;
    }

    public int getProgress() {
        return progress;
    }
}
