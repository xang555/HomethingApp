package com.xang.laothing.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.xang.laothing.Adapter.WifiListsConnectionAdapter;
import com.xang.laothing.AppState.ScanWifiResultState;
import com.xang.laothing.Model.WifiScanModel;
import com.xang.laothing.Offline.OfflineModeService;
import com.xang.laothing.Offline.response.WifiSettingResponse;
import com.xang.laothing.R;
import com.xang.laothing.Service.Depending;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingConnectWifiToSamrtDeviceActivity extends BaseActivity {


    @BindView(R.id.toolbar_and_progressbar_center_title)
    TextView toolbarAndProgressbarCenterTitle;
    @BindView(R.id.main_toolbar_and_progressbar)
    Toolbar mainToolbarAndProgressbar;
    @BindView(R.id.list_wifi_scan)
    RecyclerView listWifiScan;
    @BindView(R.id.wifi_container)
    CoordinatorLayout wifiContainer;

    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    @State(ScanWifiResultState.class)
    protected List<WifiScanModel> scanwifiResult = new ArrayList<>();
    private WifiListsConnectionAdapter connectionListsAdapter;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_connect_wifi_to_samrt_device);
        ButterKnife.bind(this);

        toolbarAndProgressbarCenterTitle.setText(R.string.setconnection_wifi_to_smartdevice);
        mainToolbarAndProgressbar.setTitle("");
        mainToolbarAndProgressbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(mainToolbarAndProgressbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        if (savedInstanceState !=null){
            SetupScanLits();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ScanWifi(); //scan wifi

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

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    void ScanWifi() {

        receiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter); // register broadcase
        wifiManager.startScan(); // start scan wifi
        SetupScanLits();  // setup lists for wifi scan

    }


    private void SetupScanLits() {

        connectionListsAdapter = new WifiListsConnectionAdapter(SettingConnectWifiToSamrtDeviceActivity.this, scanwifiResult);
        connectionListsAdapter.setOnItemClickListentner(new WifiListsConnectionAdapter.WifiAdapterListentner() {
            @Override
            public void onItemClick(WifiScanModel wifiScanModel, int position) {

                if (!wifiScanModel.getstatus()) {
                    ShowInputDialog(wifiScanModel.getSsid());
                }

            }
        });

        listWifiScan.setLayoutManager(new LinearLayoutManager(SettingConnectWifiToSamrtDeviceActivity.this));
        listWifiScan.setHasFixedSize(true);
        listWifiScan.setAdapter(connectionListsAdapter);

    } // set data to recycleview


    private void ShowInputDialog(final String ssid) {

        View view = LayoutInflater.from(SettingConnectWifiToSamrtDeviceActivity.this).inflate(R.layout.enterwifipassworddialog, null, false);
        final EditText passwordwifi = (EditText) view.findViewById(R.id.wifi_password);
        CheckBox checkshowpassword = (CheckBox) view.findViewById(R.id.check_show_password);

        checkshowpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    passwordwifi.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passwordwifi.setInputType(129);
                }

            }
        });


        alertDialog = new AlertDialog.Builder(SettingConnectWifiToSamrtDeviceActivity.this)
                .setTitle("Set " + ssid + " to Smart device")
                .setView(view)
                .setCancelable(false)
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
                        if (passwd.length() <= 0 || passwd.length() < 8) {
                            showAlert("Your Password invalid");
                        } else {
                            dialog.dismiss();

                            handleSettingSmartDevice(ssid, passwd);

                        }

                    }

                }).show();

    } // show dialog input password

    private void handleSettingSmartDevice(String ssid, String passwd) {
        // http require here

        progressDialog = Depending.showDependingProgressDialog(SettingConnectWifiToSamrtDeviceActivity.this, "connecting and setting ...");
        OfflineModeService.offlineService().SetConnectionWifi(ssid, passwd)
                .enqueue(new Callback<WifiSettingResponse>() {
                    @Override
                    public void onResponse(Call<WifiSettingResponse> call, Response<WifiSettingResponse> response) {

                        progressDialog.dismiss();

                        if (response != null && response.isSuccessful()) {

                            WifiSettingResponse wifisetting = response.body();
                            if (wifisetting.stat.equals("ok")) {
                                Snackbar.make(wifiContainer,getResources().getString(R.string.setting_wifi_suucess),Snackbar.LENGTH_LONG).show();
                            }else
                            {
                             Snackbar.make(wifiContainer,getResources().getString(R.string.setting_wifi_error),Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Snackbar.make(wifiContainer,getResources().getString(R.string.setting_wifi_error),Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<WifiSettingResponse> call, Throwable t) {
                        progressDialog.dismiss();
                      Snackbar.make(wifiContainer,t.getMessage()+",please reset smart device and try again ",Snackbar.LENGTH_LONG).show();
                    }
                });


    } // handel setting password wifi to smart device


    private void showAlert(String msg) {

        new AlertDialog.Builder(SettingConnectWifiToSamrtDeviceActivity.this)
                .setTitle("Password Invalid")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    } // show alert dialog password invalid


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
