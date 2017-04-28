package com.xang.laothing.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xang.laothing.Adapter.WifiListsConnectionAdapter;
import com.xang.laothing.Model.WifiScanModel;
import com.xang.laothing.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ConnectWifiActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_and_progressbar_center_title)
    TextView toolbarAndProgressbarCenterTitle;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.main_toolbar_and_progressbar)
    Toolbar mainToolbarAndProgressbar;
    @BindView(R.id.wifi_connection_lists)
    RecyclerView wifiConnectionLists;
    @BindView(R.id.scanb_wifi_container)
    CoordinatorLayout scanbWifiContainer;

    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private List<WifiScanModel> scanwifiResult = new ArrayList<>();
    private WifiListsConnectionAdapter connectionListsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect_wifi);
        ButterKnife.bind(this);

        toolbarAndProgressbarCenterTitle.setText(R.string.connect_smart_device_wifi);
        mainToolbarAndProgressbar.setTitle("");
        setSupportActionBar(mainToolbarAndProgressbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectWifiActivityPermissionsDispatcher.ScanWifiWithCheck(ConnectWifiActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (wifiManager!=null){
            if (!wifiManager.isWifiEnabled()) {
                showenablewifi();
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ConnectWifiActivityPermissionsDispatcher.onRequestPermissionsResult(ConnectWifiActivity.this,requestCode,grantResults);
    }


    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE})
    void ScanWifi() {

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {

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
        };

        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan(); // start scan wifi
        SetupScanLits();  // setup lists for wifi scan

    }


    private void SetupScanLits(){

        connectionListsAdapter = new WifiListsConnectionAdapter(ConnectWifiActivity.this,scanwifiResult);
        wifiConnectionLists.setLayoutManager(new LinearLayoutManager(ConnectWifiActivity.this));
        wifiConnectionLists.setHasFixedSize(true);
        wifiConnectionLists.setAdapter(connectionListsAdapter);

    } // set data to recycleview


    private boolean getConnectStatus(String bssid){

        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){

                if (networkInfo.isConnected()){

                     WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                     WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo!=null && connectionInfo.getBSSID().equals(bssid)){
                        return true;
                    }
                }

            }

        }

        return false;

    } // get status connection for bssid



    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE})
    void showrequirepermission(final PermissionRequest request) {

        new AlertDialog.Builder(this)
                .setMessage("this app want to access Wifi")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();

    }


    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE})
    void onpermissiondenied() {

        Snackbar.make(scanbWifiContainer,R.string.promission_wifi_denied,Snackbar.LENGTH_LONG).show();
        finish();

    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE})
    void onNeverAskAgain() {

        Snackbar.make(scanbWifiContainer,R.string.promission_wifi_neverask,Snackbar.LENGTH_LONG).show();
        finish();

    }


    private void showenablewifi() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ConnectWifiActivity.this)
                .setTitle("Enable Wifi")
                .setMessage("Please Enable Wifi And Connect to your smart device wifi name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        wifiManager.setWifiEnabled(true);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setCancelable(false);

        dialog.show();

    } // show dialog for enable wifi if not yet


}
