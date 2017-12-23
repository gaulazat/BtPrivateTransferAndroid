package galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransfersample.BluetoothService;
import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.models.BtDeviceSubject;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Azat on 13.11.17.
 */

public class DeviceListHolder extends RecyclerView.ViewHolder{

    private TextView textView;
    private TextView status;
    private Disposable statusDisposable;
    private BtDevice btDevice;
    public DeviceListHolder(View itemView) {
        super(itemView);
        textView = (TextView)itemView.findViewById(R.id.device_name);
        status = (TextView)itemView.findViewById(R.id.device_status);
    }

    public void attach(final BtDevice btDevice, final ClickListenerWrapper clickListenerWrapper){
        this.btDevice = btDevice;
        String name = btDevice.getDevice().getName();
        if (TextUtils.isEmpty(name)){
            name = btDevice.getDevice().getAddress();
        }
        textView.setText(name);
        status.setText(btDevice.isPaired() ? "paired" : "not paired");
        status.setTextColor(btDevice.isPaired() ? Color.GREEN : Color.RED);
        itemView.setOnClickListener(new DeviceClickListener(btDevice, clickListenerWrapper));
        if (BluetoothService.getSharedService()!= null){
            statusDisposable = BluetoothService.getSharedService().getConnectedSubject()
                    .filter(new Predicate<BtDeviceSubject>() {
                        @Override
                        public boolean test(@NonNull BtDeviceSubject btDeviceSubject) throws Exception {
                            return btDeviceSubject.getDevice().getId() == btDevice.getId();
                        }
                    })
                    .subscribe(new Consumer<BtDeviceSubject>() {
                        @Override
                        public void accept(BtDeviceSubject btDeviceSubject) throws Exception {
                            status.setText(btDeviceSubject.isConnected() ? "connected" : btDevice.isPaired() ?"paired" : "not paired");
                            status.setTextColor(btDeviceSubject.isConnected() ? Color.BLUE : btDevice.isPaired() ? Color.GREEN : Color.RED);
                        }
                    });
        }
    }

    private class DeviceClickListener implements View.OnClickListener{
        private BtDevice mBtDevice;
        private ClickListenerWrapper clickListenerWrapper;

        public DeviceClickListener(BtDevice mBtDevice, ClickListenerWrapper clickListenerWrapper) {
            this.mBtDevice = mBtDevice;
            this.clickListenerWrapper = clickListenerWrapper;
        }

        @Override
        public void onClick(View v) {
            if (clickListenerWrapper.getClickListener() != null){
                clickListenerWrapper.getClickListener().onClick(mBtDevice);
            }
        }
    }
}
