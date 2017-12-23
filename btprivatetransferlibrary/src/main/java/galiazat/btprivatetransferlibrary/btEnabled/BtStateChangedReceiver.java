package galiazat.btprivatetransferlibrary.btEnabled;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azat on 14.11.17.
 */

public class BtStateChangedReceiver extends BroadcastReceiver{

    private List<BtStateChangedCallback> changedCallbacks = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            boolean isEnabled = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    == BluetoothAdapter.STATE_ON;
            for (BtStateChangedCallback changedCallback : changedCallbacks){
                if (isEnabled){
                    changedCallback.onEnabled();
                } else {
                    changedCallback.onDisabled();
                }
            }
        }
    }

    public void addCallback(BtStateChangedCallback changedCallback){
        changedCallbacks.add(changedCallback);
    }

    public void removeCallback(BtStateChangedCallback changedCallback){
        changedCallbacks.remove(changedCallback);
    }

    public void destroy() {
        changedCallbacks = new ArrayList<>();
    }
}
