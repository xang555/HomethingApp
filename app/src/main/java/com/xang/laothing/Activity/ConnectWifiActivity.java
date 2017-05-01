package com.xang.laothing.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

    private String desiredMacAddress = "bssid";
    private ProgressDialog wificonnecting;
    private AlertDialog alertDialog;
    private int netId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect_wifi);
        ButterKnife.bind(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {

       getMenuInflater().inflate(R.menu.menu_for_connect_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if ( id == android.R.id.home){
            finish();
        }else if (id == R.id.next_setting){
            Intent intent = new Intent(ConnectWifiActivity.this,SettingConnectWifiToSamrtDeviceActivity.class);
            startActivity(intent);
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

        receiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter ); // register broadcase
        wifiManager.startScan(); // start scan wifi
        SetupScanLits();  // setup lists for wifi scan

    }


    private void SetupScanLits(){

        connectionListsAdapter = new WifiListsConnectionAdapter(ConnectWifiActivity.this,scanwifiResult);
        connectionListsAdapter.setOnItemClickListentner(new WifiListsConnectionAdapter.WifiAdapterListentner() {
            @Override
            public void onItemClick(WifiScanModel wifiScanModel, int position) {

               if (!wifiScanModel.getstatus()){
                desiredMacAddress = wifiScanModel.getBssid();
                ShowInputDialog(wifiScanModel.getSsid());
            }

            }
        });

        wifiConnectionLists.setLayoutManager(new LinearLayoutManager(ConnectWifiActivity.this));
        wifiConnectionLists.setHasFixedSize(true);
        wifiConnectionLists.setAdapter(connectionListsAdapter);

    } // set data to recycleview


    class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (SupplicantState.isValidState(state)) {

                    if (state == SupplicantState.COMPLETED){
                        boolean connected = checkConnectedToDesiredWifi();
                        if (connected){
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   wificonnecting.dismiss();
                               }
                           });
                        }

                    }

                }

                int stateerror = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,0);

                if (stateerror == WifiManager.ERROR_AUTHENTICATING){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wifiManager.disableNetwork(netId);
                            wifiManager.disconnect();
                            wificonnecting.dismiss();
                            showWifiAuthErrorDialog();
                        }
                    });

                }

            }


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

        private boolean checkConnectedToDesiredWifi() {
            boolean connected = false;

            WifiInfo wifi = wifiManager.getConnectionInfo();
            if (wifi != null) {
                // get current router Mac address
                String bssid = wifi.getBSSID();
                connected = desiredMacAddress.equals(bssid);
            }

            return connected;
        }


    } //WifiBroadcastReceiver class


    private void ShowInputDialog(final String ssid) {

        View view = LayoutInflater.from(ConnectWifiActivity.this).inflate(R.layout.enterwifipassworddialog, null, false);
        final EditText passwordwifi = (EditText)view.findViewById(R.id.wifi_password);
        CheckBox checkshowpassword= (CheckBox)view.findViewById(R.id.check_show_password);

        checkshowpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    passwordwifi.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    passwordwifi.setInputType(129);
                }

            }
        });


        alertDialog = new AlertDialog.Builder(ConnectWifiActivity.this)
                .setTitle(ssid)
                .setView(view)
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String passwd = passwordwifi.getText().toString().trim();
                        if (passwd.length() <=0 || passwd.length() < 8 ){
                        Snackbar.make(scanbWifiContainer,R.string.Correct_password_wifi,Snackbar.LENGTH_LONG).show();
                         return;
                        }else {
                            dialog.dismiss();
                            ConnectWifi(ssid,passwd);
                            showprogrssConnecting(ssid);
                        }

                    }

                }).show();

    } // show dialog input password


    private void ConnectWifi(String ssid, String password) {

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\"";
        wifiConfiguration.preSharedKey = "\"" + password + "\"";

        wifiManager.addNetwork(wifiConfiguration);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {

                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                netId = i.networkId;
                wifiManager.reconnect();

                break;
            }
        }


    } // connect to wifi

    private void showWifiAuthErrorDialog(){

        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(ConnectWifiActivity.this)
                .setTitle("Connection Failed")
                .setMessage(R.string.authentication_wifi_error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    } //show auth error


    private void showprogrssConnecting(String ssid){

        wificonnecting = new ProgressDialog(ConnectWifiActivity.this);
        wificonnecting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wificonnecting.setMessage("Connecting to " + ssid);
        wificonnecting.setCancelable(false);
        wificonnecting.show();

    } // show progress connecting to specific wifi


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
