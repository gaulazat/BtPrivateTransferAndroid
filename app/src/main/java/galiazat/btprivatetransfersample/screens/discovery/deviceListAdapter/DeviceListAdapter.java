package galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import galiazat.btprivatetransferlibrary.model.BtDevice;
import galiazat.btprivatetransfersample.R;

/**
 * Created by Azat on 13.11.17.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int TYPE_PROGRESS = 0;
    private final int TYPE_DEVICE = 1;

    private List<BtDevice> devices;
    private boolean isSearching = false;
    private ClickListenerWrapper clickListener;

    public DeviceListAdapter(List<BtDevice> devices, HolderClickListener clickListener) {
        this.devices = devices;
        this.clickListener = new ClickListenerWrapper(clickListener);
    }

    public void addDevice(BtDevice btDevice){
        devices.add(btDevice);
        notifyDataSetChanged();
    }

    public void setSearching(boolean searching) {
        if (isSearching != searching){
            isSearching = searching;
            if (isSearching){
                notifyItemInserted(0);
            } else {
                notifyItemRemoved(0);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        if (viewType == TYPE_DEVICE){
            v = inflater.inflate(R.layout.holder_device_item, parent, false);
            return new DeviceListHolder(v);
        }
        v = inflater.inflate(R.layout.holder_progress, parent, false);
        return new ProgressHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isSearching){
            if (position == 0){
                return;
            }
            position--;
        }
        ((DeviceListHolder)holder).attach(devices.get(position), clickListener);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isSearching){
            return TYPE_PROGRESS;
        }
        return TYPE_DEVICE;
    }

    @Override
    public int getItemCount() {
        int count = devices.size();
        return isSearching ? count + 1 : count;
    }

    public void clear(){
        clickListener.clear();
    }

    public void clearData() {
        devices = new ArrayList<>();
        notifyDataSetChanged();
    }
}
