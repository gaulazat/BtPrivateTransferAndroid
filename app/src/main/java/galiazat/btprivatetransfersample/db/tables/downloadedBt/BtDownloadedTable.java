package galiazat.btprivatetransfersample.db.tables.downloadedBt;

import android.database.sqlite.SQLiteDatabase;

import galiazat.btprivatetransfersample.db.tables.base.DownloadedColumns;

/**
 * Created by Azat on 27.11.17.
 */

public class BtDownloadedTable {

    public static final String NAME = "bt_table";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NAME + " ("+ DownloadedColumns.ID_COLUMNS +" INTEGER PRIMARY KEY,\n"
                + DownloadedColumns.NAME_COLUMNS +" TEXT,\n"
                + DownloadedColumns.PATH_COLUMNS +" TEXT,\n"
                + DownloadedColumns.DATE_COLUMNS +" TEXT);");
    }

}
