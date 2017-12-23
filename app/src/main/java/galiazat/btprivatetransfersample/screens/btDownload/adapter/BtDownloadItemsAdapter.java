package galiazat.btprivatetransfersample.screens.btDownload.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.models.downloaded.BtDownloadedItem;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.ClickListenerWrapper;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.HolderClickListener;

/**
 * Created by Azat on 11.12.17.
 */

public class BtDownloadItemsAdapter extends RecyclerView.Adapter<BtDownloadItemHolder>{

    private ArrayList<BtDownloadedItem> devices;

    public BtDownloadItemsAdapter(ArrayList<BtDownloadedItem> devices) {
        this.devices = devices;
    }

    @Override
    public BtDownloadItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        v = inflater.inflate(R.layout.holder_downloaded_item, parent, false);
        return new BtDownloadItemHolder(v);
    }

    @Override
    public void onBindViewHolder(BtDownloadItemHolder holder, int position) {
        holder.attach(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void updateData(ArrayList<BtDownloadedItem> netDownloadedItems) {
        devices = netDownloadedItems;
        notifyDataSetChanged();
    }

    public void addItem(BtDownloadedItem item){
        devices.add(item);
        notifyItemInserted(devices.size() - 1);
    }

    public ArrayList<BtDownloadedItem> getItems() {
        return devices;
    }
}
