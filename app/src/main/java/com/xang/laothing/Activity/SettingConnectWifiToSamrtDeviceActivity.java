package com.xang.laothing.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.xang.laothing.Adapter.WifiListsConnectionAdapter;
import com.xang.laothing.Model.WifiScanModel;
import com.xang.laothing.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingConnectWifiToSamrtDeviceActivity extends AppCompatActivity {


    @BindView(R.id.toolbar_and_progressbar_center_title)
    TextView toolbarAndProgressbarCenterTitle;
    @BindView(R.id.main_toolbar_and_progressbar)
    Toolbar mainToolbarAndProgressbar;
    @BindView(R.id.list_wifi_scan)
    RecyclerView listWifiScan;

    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private List<WifiScanModel> scanwifiResult = new ArrayList<>();
    private WifiListsConnectionAdapter connectionListsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_connect_wifi_to_samrt_device);
        ButterKnife.bind(this);

        toolbarAndProgressbarCenterTitle.setText(R.string.setconnection_wifi_to_smartdevice);
        mainToolbarAndProgressbar.setTitle("");
        setSupportActionBar(mainToolbarAndProgressbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    }


    @Override
    protected void onStart() {
        super.onStart();

        ScanWifi(); //scan wifi

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    void ScanWifi() {

        receiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter ); // register broadcase
        wifiManager.startScan(); // start scan wifi
        SetupScanLits();  // setup lists for wifi scan

    }


    private void SetupScanLits(){

        connectionListsAdapter = new WifiListsConnectionAdapter(SettingConnectWifiToSamrtDeviceActivity.this,scanwifiResult);
        connectionListsAdapter.setOnItemClickListentner(new WifiListsConnectionAdapter.WifiAdapterListentner() {
            @Override
            public void onItemClick(WifiScanModel wifiScanModel, int position) {


            }
        });

        listWifiScan.setLayoutManager(new LinearLayoutManager(SettingConnectWifiToSamrtDeviceActivity.this));
        listWifiScan.setHasFixedSize(true);
        listWifiScan.setAdapter(connectionListsAdapter);

    } // set data to recycleview



    class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                List<ScanResult> scanResults = wifiManager.getScanResults();

                scanwifiResult.clear();

                for (ScanResult result : scanResults) {

                    WifiScanModel scanModel = new WifiScanModel();
                    scanModel.setBssid(result.BSSID);
                    scanModel.setSsid(result.SSID);
                    scanModel.setStatus(getConnectStatus(result.BSSID));
                    scanwifiResult.add(scanModel);

                }

                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        connectionListsAdapter.notifyDataSetChanged();
                    }
                });

            }

        }


    } //WifiBroadcastReceiver class


    private boolean getConnectStatus(String bssid) {

        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                if (networkInfo.isConnected()) {

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo != null && connectionInfo.getBSSID().equals(bssid)) {
                        return true;
                    }
                }

            }

        }

        return false;

    } // get status connection for bssid


}
