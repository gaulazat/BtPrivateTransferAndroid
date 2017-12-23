package galiazat.btprivatetransfersample.db.tables;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.impl.DefaultStorIOSQLite;

import galiazat.btprivatetransfersample.App;
import galiazat.btprivatetransfersample.db.DbOpenHelper;

/**
 * Created by Azat on 27.11.17.
 */

public class DbModule {

    private static StorIOSQLite storIOSQLite;

    public static StorIOSQLite provideStorIOSQLite() {
        if (storIOSQLite == null) {
            storIOSQLite = DefaultStorIOSQLite.builder()
                    .sqliteOpenHelper(provideSQLiteOpenHelper(App.get()))
                    .build();
        }
        return storIOSQLite;
    }

    private static SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }


}