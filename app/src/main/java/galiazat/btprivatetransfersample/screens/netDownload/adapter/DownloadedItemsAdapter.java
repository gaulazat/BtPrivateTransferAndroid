package galiazat.btprivatetransfersample.screens.netDownload.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import galiazat.btprivatetransfersample.R;
import galiazat.btprivatetransfersample.models.downloaded.NetDownloadedItem;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.ClickListenerWrapper;
import galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter.HolderClickListener;

/**
 * Created by Azat on 27.11.17.
 */

public class DownloadedItemsAdapter extends RecyclerView.Adapter<DownloadeditemHolder>{

    private ArrayList<NetDownloadedItem> devices;
    private ClickListenerWrapper<NetDownloadedItem> clickListener;

    public DownloadedItemsAdapter(ArrayList<NetDownloadedItem> devices, HolderClickListener<NetDownloadedItem> clickListener) {
        this.devices = devices;
        this.clickListener = new ClickListenerWrapper(clickListener);
    }

    @Override
    public DownloadeditemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        v = inflater.inflate(R.layout.holder_downloaded_item, parent, false);
        return new DownloadeditemHolder(v);
    }

    @Override
    public void onBindViewHolder(DownloadeditemHolder holder, int position) {
        holder.attach(devices.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void clear(){
        clickListener.clear();
    }

    public void updateData(ArrayList<NetDownloadedItem> netDownloadedItems) {
        devices = netDownloadedItems;
        notifyDataSetChanged();
    }

    public void addItem(NetDownloadedItem item){
        devices.add(item);
        notifyItemInserted(devices.size() - 1);
    }
}
