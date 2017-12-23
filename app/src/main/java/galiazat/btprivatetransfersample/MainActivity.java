package galiazat.btprivatetransfersample;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.io.File;

import galiazat.btprivatetransfersample.screens.btDownload.BtDownloadFragment;
import galiazat.btprivatetransfersample.screens.discovery.BtDiscoveryFragment;
import galiazat.btprivatetransfersample.screens.netDownload.NetDownloadFragment;

public class MainActivity extends FragmentActivity{

    public static MainActivity sActivity;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    BtDiscoveryFragment.startFragment(MainActivity.this);
                    return true;
                case R.id.navigation_dashboard:
                    NetDownloadFragment.startFragment(MainActivity.this);
                    return true;
                case R.id.navigation_notifications:
                    BtDownloadFragment.startFragment(MainActivity.this);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sActivity = this;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            BtDiscoveryFragment.startFragmentFirst(this);
            initDiscoverableMode();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initDiscoverableMode() {
        Intent discoverableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sActivity = null;
    }
}
