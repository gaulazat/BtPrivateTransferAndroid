package galiazat.btprivatetransfersample.screens.netDownload.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransfersample.BluetoothService;
import galiazat.btprivatetransfersample.DownloadService;
import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.models.BtDeviceSubject;
import galiazat.btprivatetransfersample.models.EventDownloadProgress;
import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItem;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.ClickListenerWrapper;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.DeviceListHolder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Azat on 27.11.17.
 */

public class DownloadeditemHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ProgressBar progressBar;
    private Disposable progressDisposable;
    public DownloadeditemHolder(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.device_name);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
    }

    public void attach(final NetDownloadedItem item, final ClickListenerWrapper<NetDownloadedItem> clickListenerWrapper){
        String name =item.name;
        textView.setText(name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListenerWrapper!=null){
                    clickListenerWrapper.getClickListener().onClick(item);
                }
            }
        });
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        if (DownloadService.getDownloadService()!= null){
            progressDisposable = DownloadService.getDownloadService().getDownloadProgressSubject()
                    .filter(new Predicate<EventDownloadProgress>() {
                        @Override
                        public boolean test(@NonNull EventDownloadProgress eventDownloadProgress) throws Exception {
                            return eventDownloadProgress.getItem().id.intValue() == item.id.intValue();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<EventDownloadProgress>() {
                        @Override
                        public void accept(EventDownloadProgress eventDownloadProgress) throws Exception {
                            if (eventDownloadProgress.getProgress() == 100){
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                progressBar.setProgress(eventDownloadProgress.getProgress());
                            }
                        }
                    });
        }
    }

}
