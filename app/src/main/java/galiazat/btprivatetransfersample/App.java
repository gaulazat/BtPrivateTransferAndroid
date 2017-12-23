package galiazat.btprivatetransfersample;

import android.app.Application;
import android.content.Intent;

import java.io.File;

/**
 * Created by Azat on 14.11.17.
 */

public class App extends Application {

    private static App sInstance;

    public static String MAIN_FOLDER_PATH;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        startServices();
        MAIN_FOLDER_PATH = getApplicationInfo().dataDir +
                File.separator + "main";

    }

    private void startServices() {
        BluetoothService playerService = BluetoothService.getSharedService();
        if (playerService == null) {
            startService(new Intent(getApplicationContext(), BluetoothService.class));
        }

        DownloadService downloadService = DownloadService.getDownloadService();
        if (downloadService == null) {
            startService(new Intent(getApplicationContext(), DownloadService.class));
        }
    }

    static public App get(){
        return sInstance;
    }

}
