package galiazat.btprivatetransfersample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import galiazat.btprivatetransfersample.db.tables.downloadedBt.BtDownloadedTable;
import galiazat.btprivatetransfersample.db.tables.downloadedNet.NetDownloadedTable;

/**
 * Created by Azat on 27.11.17.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION  = 4;

    public DbOpenHelper(@NonNull Context context) {
        super(context, "main_db", null, VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        BtDownloadedTable.createTable(db);
        NetDownloadedTable.createTable(db);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
