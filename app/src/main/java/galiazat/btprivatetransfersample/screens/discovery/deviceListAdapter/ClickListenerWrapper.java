package galiazat.btprivatetransfersample.screens.discovery.deviceListAdapter;

/**
 * Created by Azat on 14.11.17.
 */

public class ClickListenerWrapper<T> {

    private HolderClickListener<T> clickListener;

    public ClickListenerWrapper(HolderClickListener<T> clickListener) {
        this.clickListener = clickListener;
    }

    public HolderClickListener<T> getClickListener() {
        return clickListener;
    }

    public void clear(){
        clickListener = null;
    }
}
