package galiazat.btprivatetransferlibrary.discovery;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.view.View;

import java.util.Set;

import galiazat.btprivatetransferlibrary.exceptions.ActivateBtException;
import galiazat.btprivatetransferlibrary.exceptions.PermissionDeniedException;
import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransferlibrary.util.StaticIdGenerator;

/**
 * Created by Azat on 12.11.17.
 */

public class BtDeviceDiscovery {

    private DeviceDiscoveryCallback mDeviceDiscoveryCallback;
    private ContextWrapper mContext;
    private BtFoundDeviceReceiver mReceiver;

    protected BtDeviceDiscovery(){}

    protected void setDeviceDiscoveryCallback(DeviceDiscoveryCallback mDeviceDiscoveryCallback) {
        this.mDeviceDiscoveryCallback = mDeviceDiscoveryCallback;
    }

    protected void setContext(ContextWrapper mContext) {
        this.mContext = mContext;
    }

    public void start(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_DENIED){
                if (mDeviceDiscoveryCallback != null){
                    mDeviceDiscoveryCallback.discoveryFinished(new PermissionDeniedException("ACCESS_COARSE_LOCATION"));
                }
            }
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()){
            if (!bluetoothAdapter.enable()){
                if (mDeviceDiscoveryCallback != null){
                    mDeviceDiscoveryCallback.discoveryFinished(new ActivateBtException());
                }
                return;
            }
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            if (mDeviceDiscoveryCallback != null){
                for (BluetoothDevice device : pairedDevices) {
                    BtDevice btDevice = new BtDevice(device, StaticIdGenerator.getInstance().generateId(), true);
                    mDeviceDiscoveryCallback.deviceFounded(btDevice);
                }
            }
        }
        stop();
        if (mContext != null) {
            mReceiver = new BtFoundDeviceReceiver(new DeviceDiscoveryCallbackWrapper(mDeviceDiscoveryCallback, new UtilDiscoveryCallback() {
                @Override
                public void onFindFinshed() {
                    stop();
                }
            }));
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(mReceiver, filter);
            bluetoothAdapter.startDiscovery();
        }
    }

    public void stop(){
        if (mReceiver != null){
            mReceiver.destroy();
            if (mContext != null) {
                mContext.unregisterReceiver(mReceiver);
            }
            mReceiver = null;
        }
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    public void destroy(){
        stop();
        mContext = null;
        mDeviceDiscoveryCallback = null;
    }

    public static class Builder{

        private DeviceDiscoveryCallback mDeviceDiscoveryCallback;
        private ContextWrapper mContext;

        public Builder setDeviceDiscoveryCallback(DeviceDiscoveryCallback mDeviceDiscoveryCallback) {
            this.mDeviceDiscoveryCallback = mDeviceDiscoveryCallback;
            return this;
        }

        public Builder setContext(ContextWrapper context) {
            this.mContext = context;
            return this;
        }

        public BtDeviceDiscovery build(){
            BtDeviceDiscovery btDeviceDiscovery = new BtDeviceDiscovery();
            btDeviceDiscovery.setContext(mContext);
            btDeviceDiscovery.setDeviceDiscoveryCallback(mDeviceDiscoveryCallback);
            return btDeviceDiscovery;
        }

    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private class BtFoundDeviceReceiver extends BroadcastReceiver{

        DeviceDiscoveryCallbackWrapper callbackWrapper;

        public BtFoundDeviceReceiver(DeviceDiscoveryCallbackWrapper callbackWrapper) {
            this.callbackWrapper = callbackWrapper;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (callbackWrapper != null){
                        BtDevice btDevice = new BtDevice(device, StaticIdGenerator.getInstance().generateId());
                        callbackWrapper.deviceFounded(btDevice);
                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                callbackWrapper.deviceFoundFinished();
            }
        }

        public void destroy(){
            if (callbackWrapper != null){
                callbackWrapper.destroy();
            }
            callbackWrapper = null;
        }
    }

}
