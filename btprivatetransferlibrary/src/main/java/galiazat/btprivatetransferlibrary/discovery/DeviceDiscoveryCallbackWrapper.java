package galiazat.btprivatetransferlibrary.discovery;

import galiazat.btprivatetransferlibrary.model.BtDevice;

/**
 * Created by Azat on 12.11.17.
 */

public class DeviceDiscoveryCallbackWrapper {

    private DeviceDiscoveryCallback mCallback;
    private boolean mIsFinished = false;
    private UtilDiscoveryCallback mUtilDiscoveryCallback;

    public DeviceDiscoveryCallbackWrapper(DeviceDiscoveryCallback mCallback, UtilDiscoveryCallback mUtilDiscoveryCallback) {
        this.mCallback = mCallback;
        this.mUtilDiscoveryCallback = mUtilDiscoveryCallback;
    }

    public void deviceFounded(BtDevice device){
        if (mCallback != null){
            mCallback.deviceFounded(device);
        }
    }

    public void deviceFoundFinished(){
        mIsFinished = true;
        if (mCallback != null){
            mCallback.discoveryFinished(null);
        }
        if (mUtilDiscoveryCallback != null){
            mUtilDiscoveryCallback.onFindFinshed();
        }
    }

    public void destroy(){
        mCallback = null;
        mUtilDiscoveryCallback = null;
    }

}
