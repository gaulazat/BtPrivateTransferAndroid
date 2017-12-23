package galiazat.btprivatetransfersample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;

import galiazat.btprivatetransfersample.models.EventDownloadProgress;
import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItem;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by Azat on 27.11.17.
 */

public class DownloadService extends Service{

    private static final String TAG = DownloadService.class.getSimpleName();

    private static DownloadService downloadService ;
    private ReplaySubject<EventDownloadProgress> downloadProgressSubject = ReplaySubject.createWithSize(100);
    private boolean isNowDownLoading;

    private DownloadCallback downloadCallback;

    public static DownloadService getDownloadService() {
        return downloadService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadService = this;
        return START_NOT_STICKY ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadService = this;
    }

    public void downloadFile(final NetDownloadedItem downloadedItem, String url) {
            final File fileN = new File(downloadedItem.path);
            if (!fileN.exists()) {
                try {
                    fileN.createNewFile();
//                    L.d("FilePath", fileN.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fileN, true);
            } catch (FileNotFoundException e1) {
                // TODO handle it
            }
            Log.d("FileDownlaod", "FileDownlaod");
            Ion.with(getApplicationContext())
                    .load(url)
                    .progress(new ProgressCallback() {
                        int prevProgress = 0;

                        @Override
                        public void onProgress(long downloaded, long total) {
//                            L.d("FileDownlaod", "downloaded  = " + downloaded + " / " + total);

                            int progressValue = (int) (downloaded * 100 / total);
                            if (prevProgress < progressValue) {
                                prevProgress = progressValue;
                                if (progressValue % 10 == 0) {
                                    downloadProgressSubject.onNext(new EventDownloadProgress(downloadedItem, progressValue));
                                }
                            }
                        }
                    })
                    .noCache()
                    .write(fos)
                    .setCallback(new FutureCallback<FileOutputStream>() {
                        @Override
                        public void onCompleted(Exception e, FileOutputStream file) {
//                            Log.d("DownloadedFileSize", fileN.length() + "/" + currentTotal);
//                            itemInProcess = null;
                            isNowDownLoading = false;
                            if (e == null) {
                                if (downloadCallback != null) {
                                    downloadCallback.onDownloadEnded(downloadedItem);
                                }
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
    }

    public static void removeDownloadObservable() {
        if (downloadService!=null){
            downloadService.downloadProgressSubject=ReplaySubject.createWithSize(1);
        }
    }

    public boolean isNowDownLoading() {
        return isNowDownLoading;
    }

    public void setDownloadCallback(DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    public ReplaySubject<EventDownloadProgress> getDownloadProgressSubject() {
        return downloadProgressSubject;
    }
}
