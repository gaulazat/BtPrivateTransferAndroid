package galiazat.btprivatetransfersample.screens.btDownload.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import galiazat.btprivatetransfersample.BluetoothService;
import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.models.BtDownloadEvent;
import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Azat on 11.12.17.
 */

public class BtDownloadItemHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ProgressBar progressBar;
    private Disposable progressDisposable;
    public BtDownloadItemHolder(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.device_name);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
    }

    public void attach(final BtDownloadedItem item){
        String name =item.name;
        textView.setText(name);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        if (BluetoothService.getSharedService()!= null){
            progressDisposable = BluetoothService.getSharedService().getDownloadSubject()
                    .filter(new Predicate<BtDownloadEvent>() {
                        @Override
                        public boolean test(@NonNull BtDownloadEvent BtDownloadEvent) throws Exception {
                            return BtDownloadEvent.getItem().name.equals(item.name);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BtDownloadEvent>() {
                        @Override
                        public void accept(BtDownloadEvent eventDownloadProgress) throws Exception {
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
