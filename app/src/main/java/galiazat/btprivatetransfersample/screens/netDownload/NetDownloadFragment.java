package galiazat.btprivatetransfersample.screens.netDownload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import galiazat.btprivatetransferlibrary.model.TransferModel;
import galiazat.btprivatetransferlibrary.util.StaticIdGenerator;
import galiazat.btprivatetransfersample.App;
import galiazat.btprivatetransfersample.BluetoothService;
import galiazat.btprivatetransfersample.DownloadCallback;
import galiazat.btprivatetransfersample.DownloadService;
import galiazat.btprivatetransfersample.MainActivity;
import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.db.tables.DbModule;
import galiazat.btprivatetransfersample.db.tables.downloadedNet.NetDownloadedTable;
import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItem;
import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItemStorIOSQLiteGetResolver;
import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItemStorIOSQLitePutResolver;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.HolderClickListener;
import galiazat.btprivatetransfersample.screens.netDownload.adapter.DownloadedItemsAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Azat on 27.11.17.
 */

public class NetDownloadFragment extends Fragment implements DownloadCallback{

    private static String TAG = NetDownloadFragment.class.getName();

    @BindView(R.id.edit_text_url)
    EditText editUrl;
    @BindView(R.id.edit_text_name)
    EditText editFileName;
    @BindView(R.id.download)
    Button downloadButton;
    @BindView(R.id.device_list)
    RecyclerView mDeviceList;
    DownloadedItemsAdapter mDeviceListAdapter;
    private int lastDownloadedFileId = 0;

    public static void startFragmentFirst(MainActivity coreActivity) {
        FragmentTransaction transaction = (coreActivity).getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out) ;
        transaction.add(R.id.activity_core_main_content, new NetDownloadFragment(), TAG);
        transaction.commitAllowingStateLoss();
    }

    public static void startFragment(MainActivity coreActivity) {
        FragmentTransaction transaction = (coreActivity).getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out) ;
        transaction.replace(R.id.activity_core_main_content, new NetDownloadFragment(), TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_net_download, container, false);
        ButterKnife.bind(this, root);
        downloadButton.setEnabled(true);
        initList();
        startReadFromDb();
        if (DownloadService.getDownloadService()!=null){
            DownloadService.getDownloadService().setDownloadCallback(this);
        }
        return root;
    }

    private void startReadFromDb() {
        Observable.just(new Object())
                .map(new Function<Object, List<NetDownloadedItem>>() {
                    @Override
                    public List<NetDownloadedItem> apply(@NonNull Object o) throws Exception {
                        return DbModule.provideStorIOSQLite().get()
                                .listOfObjects(NetDownloadedItem.class)
                                .withQuery(RawQuery.builder()
                                        .query("SELECT * from " + NetDownloadedTable.NAME)
                                        .build())
                                .withGetResolver(new NetDownloadedItemStorIOSQLiteGetResolver())
                                .prepare()
                                .executeAsBlocking();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NetDownloadedItem>>() {
                    @Override
                    public void accept(List<NetDownloadedItem> netDownloadedItems) throws Exception {
                        mDeviceListAdapter.updateData(new ArrayList<NetDownloadedItem>(netDownloadedItems));
                        if (netDownloadedItems.size()!=0){
                            lastDownloadedFileId = netDownloadedItems.get(netDownloadedItems.size() - 1).id;
                        }
                    }
                });
    }

    private void initList() {
        if (mDeviceListAdapter == null) {
            mDeviceListAdapter = new DownloadedItemsAdapter(new ArrayList<NetDownloadedItem>(), new HolderClickListener<NetDownloadedItem>() {
                @Override
                public void onClick(NetDownloadedItem device) {
                    if (BluetoothService.getSharedService() != null) {
                        if (!BluetoothService.getSharedService().isConnected()) {
                            Toast.makeText(App.get(), "not connected to device", Toast.LENGTH_LONG).show();
                            return;
                        }
                        TransferModel transferModel = new TransferModel(device.name);
                        transferModel.setPath(device.path);
                        BluetoothService.getSharedService().sendFile(transferModel);
                    }
                }
            });
        }
        mDeviceList.setLayoutManager(new LinearLayoutManager(App.get()));
        mDeviceList.setAdapter(mDeviceListAdapter);
    }

    @OnClick(R.id.download)
    public void onDownloadClick(){
        downloadButton.setEnabled(false);
        String url = editUrl.getText().toString();
        String fileName = editFileName.getText().toString();
        if (TextUtils.isEmpty(url)){
            Toast.makeText(App.get(), "write url", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(fileName)){
            Toast.makeText(App.get(), "write downloaded file name", Toast.LENGTH_LONG).show();
            return;
        }
        if (DownloadService.getDownloadService()!= null){
            if (DownloadService.getDownloadService().isNowDownLoading()){
                Toast.makeText(App.get(), "already downloading", Toast.LENGTH_LONG).show();
            } else {
                NetDownloadedItem downloadedItem = new NetDownloadedItem();
                File mainFolder = new File(App.MAIN_FOLDER_PATH);
                if (!mainFolder.exists()) {
                    mainFolder.mkdirs();
                }
                final File fileN = new File(mainFolder, fileName);
                if (fileN.exists()){
                    Toast.makeText(App.get(), "file with what name exists", Toast.LENGTH_LONG).show();
                    return;
                }
                lastDownloadedFileId++;
                downloadedItem.id = lastDownloadedFileId;
                downloadedItem.name = fileName;
                downloadedItem.path = fileN.getPath();
                mDeviceListAdapter.addItem(downloadedItem);
                DownloadService.getDownloadService().downloadFile(downloadedItem, editUrl.getText().toString());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDeviceListAdapter != null){
            mDeviceListAdapter.clear();
        }
        if (DownloadService.getDownloadService()!= null){
            DownloadService.getDownloadService().setDownloadCallback(null);
        }
    }

    @Override
    public void onDownloadStarted(NetDownloadedItem item) {

    }

    @Override
    public void onDownloadEnded(final NetDownloadedItem item) {
        downloadButton.setEnabled(true);
        Observable.just(new Object())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(@NonNull Object o) throws Exception {
                        DbModule.provideStorIOSQLite().put()
                                .object(item)
                                .withPutResolver(new NetDownloadedItemStorIOSQLitePutResolver())
                                .prepare()
                                .executeAsBlocking();
                        return o;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        startReadFromDb();
                    }
                });
    }
}
