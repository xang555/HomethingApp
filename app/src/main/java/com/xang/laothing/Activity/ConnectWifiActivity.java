package com.xang.laothing.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xang.laothing.Adapter.WifiListsConnectionAdapter;
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
    private List<String> scanwifiResult = new ArrayList<String>();
    private WifiListsConnectionAdapter connectionListsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect_wifi);
        ButterKnife.bind(this);

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
                        scanwifiResult.add(result.SSID);
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
        wifiManager.startScan();

        connectionListsAdapter = new WifiListsConnectionAdapter(ConnectWifiActivity.this,scanwifiResult);
        wifiConnectionLists.setLayoutManager(new LinearLayoutManager(ConnectWifiActivity.this));
        wifiConnectionLists.setHasFixedSize(true);
        wifiConnectionLists.setAdapter(connectionListsAdapter);

    }

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

    }


}
