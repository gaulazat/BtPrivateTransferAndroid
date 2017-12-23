package galiazat.btprivatetransfersample.models.downloaded;

import com.pushtorefresh.storio2.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio2.sqlite.annotations.StorIOSQLiteType;

import java.util.Date;

import galiazat.btprivatetransfersample.db.tables.base.DownloadedColumns;
import galiazat.btprivatetransfersample.db.tables.downloadedBt.BtDownloadedTable;
import galiazat.btprivatetransfersample.models.downloaded.BaseDownloadedItem;

/**
 * Created by Azat on 27.11.17.
 */
@StorIOSQLiteType(table = BtDownloadedTable.NAME)
public class BtDownloadedItem extends BaseDownloadedItem {

    @StorIOSQLiteColumn(name = DownloadedColumns.ID_COLUMNS, key = true)
    public Integer id;
    @StorIOSQLiteColumn(name = DownloadedColumns.NAME_COLUMNS)
    public String name;
    @StorIOSQLiteColumn(name = DownloadedColumns.PATH_COLUMNS)
    public String path;
    @StorIOSQLiteColumn(name = DownloadedColumns.DATE_COLUMNS)
    public String dateDownloaded;

    public void setDate(Date date) {
        dateDownloaded = date.toString();
    }
}
