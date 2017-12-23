package galiazat.btprivatetransfersample.screens.discovery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import galiazat.btprivatetransferlibrary.discovery.BtDeviceDiscovery;
import galiazat.btprivatetransferlibrary.discovery.DeviceDiscoveryCallback;
import galiazat.btprivatetransferlibrary.exceptions.ActivateBtException;
import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransfersample.App;
import galiazat.btprivatetransfersample.BluetoothService;
import galiazat.btprivatetransfersample.MainActivity;
import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.HolderClickListener;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.DeviceListAdapter;

/**
 * Created by Azat on 27.11.17.
 */

public class BtDiscoveryFragment extends Fragment{

    private static final String TAG = BtDiscoveryFragment.class.getName();
    private static final int REQUEST_COARSE_LOCATION = 1;

    @BindView(R.id.device_list)
    RecyclerView mDeviceList;
    @BindView(R.id.btn_find)
    Button mFindButton;
    @BindView(R.id.btn_cancel)
    Button mCancelButton;
    private DeviceListAdapter mDeviceListAdapter;
    private BtDeviceDiscovery mDeviceDiscovery;

    public static void startFragmentFirst(MainActivity coreActivity) {
        FragmentTransaction transaction = (coreActivity).getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out) ;
        transaction.add(R.id.activity_core_main_content, new BtDiscoveryFragment(), TAG);
        transaction.commitAllowingStateLoss();
    }

    public static void startFragment(MainActivity coreActivity) {
        FragmentTransaction transaction = (coreActivity).getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out) ;
        transaction.replace(R.id.activity_core_main_content, new BtDiscoveryFragment(), TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this, root);
        setRetainInstance(true);
        mFindButton.setEnabled(true);
        mCancelButton.setEnabled(false);
        initList();
        return root;
    }

    private void initList() {
        if (mDeviceListAdapter == null) {
            mDeviceListAdapter = new DeviceListAdapter(new ArrayList<BtDevice>(), new HolderClickListener<BtDevice>() {
                @Override
                public void onClick(BtDevice device) {
                    startSendingFile(device);
                }
            });
        }
        mDeviceList.setLayoutManager(new LinearLayoutManager(App.get()));
        mDeviceList.setAdapter(mDeviceListAdapter);
    }

    private void startSendingFile(BtDevice device) {
        BluetoothService.getSharedService().connect(device);
//        TransferModel transformModel = new TransferModel("Test image");
//        try {
//
//            File f = new File(getCacheDir()+"/test.jpg");
//            if (!f.exists()) {
//                InputStream is = getAssets().open("test.jpg");
//                int size = is.available();
//                byte[] buffer = new byte[size];
//                is.read(buffer);
//                is.close();
//
//
//                FileOutputStream fos = new FileOutputStream(f);
//                fos.write(buffer);
//                fos.close();
//            }
//            transformModel.setPath(f.getPath());
//            BluetoothService.getSharedService().sendFile(device, transformModel);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @OnClick(R.id.btn_find)
    public void onFindClick(){
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.sActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }
        onStartSearch();
        if (mDeviceDiscovery == null) {
            mDeviceDiscovery = new BtDeviceDiscovery.Builder()
                    .setContext(App.get())
                    .setDeviceDiscoveryCallback(new DeviceDiscoveryCallback() {
                        @Override
                        public void deviceFounded(BtDevice device) {
                            addDevice(device);
                        }

                        @Override
                        public void discoveryFinished(Throwable t) {
                            if (t != null) {
                                if (t instanceof ActivateBtException) {
                                    Toast.makeText(App.get(), "Ошибка активации bluetooth", Toast.LENGTH_LONG).show();
                                }
                            }
                            onFinishSearch();
                        }
                    })
                    .build();
        }
        mDeviceDiscovery.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onFindClick();
                } else {

                }
                break;
            }
        }
    }
    private void addDevice(BtDevice device){
        mDeviceListAdapter.addDevice(device);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onFinishSearch();
        if (mDeviceDiscovery != null){
            mDeviceDiscovery.stop();
            mDeviceDiscovery.destroy();
            mDeviceDiscovery = null;
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelClick(){
        if (mDeviceDiscovery != null){
            mDeviceDiscovery.stop();
        }
        onFinishSearch();
    }

    private void onStartSearch(){
        mDeviceListAdapter.setSearching(true);
        mDeviceListAdapter.clearData();
        mFindButton.setEnabled(false);
        mCancelButton.setEnabled(true);
    }

    private void onFinishSearch(){
        mDeviceListAdapter.setSearching(false);
        mFindButton.setEnabled(true);
        mCancelButton.setEnabled(false);
    }

}
