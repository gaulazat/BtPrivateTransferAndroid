package galiazat.btprivatetransfersample.screens.btDownload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import galiazat.btprivatetransfersample.App;
import galiazat.btprivatetransfersample.BluetoothService;
import galiazat.btprivatetransfersample.BtDownloadCallback;
import galiazat.btprivatetransfersample.MainActivity;
import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.db.tables.DbModule;
import galiazat.btprivatetransfersample.db.tables.downloadedBt.BtDownloadedTable;
import galiazat.btprivatetransfersample.models.BtDownloadEvent;
import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItem;
import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItemStorIOSQLiteGetResolver;
import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItemStorIOSQLitePutResolver;
import galiazat.btprivatetransfersample.screens.btDownload.adapter.BtDownloadItemsAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Azat on 11.12.17.
 */

public class BtDownloadFragment extends Fragment{

    private static String TAG = BtDownloadFragment.class.getName();

    @BindView(R.id.edit_text_url)
    EditText editUrl;
    @BindView(R.id.edit_text_name)
    EditText editFileName;
    @BindView(R.id.download)
    Button downloadButton;
    @BindView(R.id.device_list)
    RecyclerView mDeviceList;
    BtDownloadItemsAdapter mDeviceListAdapter;

    Disposable disposable;

    public static void startFragmentFirst(MainActivity coreActivity) {
        FragmentTransaction transaction = (coreActivity).getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out) ;
        transaction.add(R.id.activity_core_main_content, new BtDownloadFragment(), TAG);
        transaction.commitAllowingStateLoss();
    }

    public static void startFragment(MainActivity coreActivity) {
        FragmentTransaction transaction = (coreActivity).getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out) ;
        transaction.replace(R.id.activity_core_main_content, new BtDownloadFragment(), TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_net_download, container, false);
        ButterKnife.bind(this, root);
        downloadButton.setVisibility(View.GONE);
        editFileName.setVisibility(View.GONE);
        editUrl.setVisibility(View.GONE);
        setRetainInstance(true);
        initList();
        startReadFromDb();
        return root;
    }

    private void startReadFromDb() {
        Observable.just(new Object())
                .map(new Function<Object, List<BtDownloadedItem>>() {
                    @Override
                    public List<BtDownloadedItem> apply(@NonNull Object o) throws Exception {
                        return DbModule.provideStorIOSQLite().get()
                                .listOfObjects(BtDownloadedItem.class)
                                .withQuery(RawQuery.builder()
                                        .query("SELECT * from " + BtDownloadedTable.NAME)
                                        .build())
                                .withGetResolver(new BtDownloadedItemStorIOSQLiteGetResolver())
                                .prepare()
                                .executeAsBlocking();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<BtDownloadedItem>>() {
                    @Override
                    public void accept(List<BtDownloadedItem> netDownloadedItems) throws Exception {
                        mDeviceListAdapter.updateData(new ArrayList<BtDownloadedItem>(netDownloadedItems));
                    }
                });
    }

    private void initList() {
        if (mDeviceListAdapter == null) {
            mDeviceListAdapter = new BtDownloadItemsAdapter(new ArrayList<BtDownloadedItem>());
        }
        mDeviceList.setLayoutManager(new LinearLayoutManager(App.get()));
        mDeviceList.setAdapter(mDeviceListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (BluetoothService.getSharedService()!= null){
            BluetoothService.getSharedService().setCallback(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        disposable = BluetoothService.getSharedService().getDownloadSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BtDownloadEvent>() {
                    @Override
                    public void accept(BtDownloadEvent res) throws Exception {
                        if (res.getProgress() == 100) {
                            onDownloadEnded(res.getItem());
                        } else {
                            onDownloadStarted(res.getItem());
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    public void onDownloadStarted(BtDownloadedItem item) {
        if (mDeviceListAdapter != null) {
            for (BtDownloadedItem btDownloadedItem : mDeviceListAdapter.getItems()) {
                if (btDownloadedItem.name.equals(item.name)) {
                    return;
                }
            }
            mDeviceListAdapter.addItem(item);
        }
    }


    public void onDownloadEnded(final BtDownloadedItem item) {
        downloadButton.setEnabled(true);
        item.setDate(Calendar.getInstance().getTime());
        Observable.just(new Object())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(@NonNull Object o) throws Exception {
                        DbModule.provideStorIOSQLite().put()
                                .object(item)
                                .withPutResolver(new BtDownloadedItemStorIOSQLitePutResolver())
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
