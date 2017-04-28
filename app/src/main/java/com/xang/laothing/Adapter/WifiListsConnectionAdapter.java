package com.xang.laothing.Adapter;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xang.laothing.Model.WifiScanModel;
import com.xang.laothing.R;

import java.util.List;

/**
 * Created by xang on 27/04/2017.
 */

public class WifiListsConnectionAdapter extends RecyclerView.Adapter<WifiListsConnectionAdapter.Viewhloder> {

    private List<WifiScanModel> wifiscanresult;
    private Context context;
    private AlertDialog alertDialog;
    private BroadcastReceiver broadcastReceiver;
    private String desiredMacAddress = "bssid";
    private ProgressDialog wificonnecting;
    private int netId = 0;

    public WifiListsConnectionAdapter(Context context, List<WifiScanModel> wifiscanresult) {
        this.wifiscanresult = wifiscanresult;
        this.context = context;

        broadcastReceiver = new WifiBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public Viewhloder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.wifi_connection_items, parent, false);
        Viewhloder vh = new Viewhloder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(Viewhloder holder, int position) {

        holder.ssid.setText(this.wifiscanresult.get(position).getSsid());
        if (this.wifiscanresult.get(position).getstatus()) {
            holder.ssid.setTextColor(Color.BLUE);
        }else {
            holder.ssid.setTextColor(Color.BLACK);
        }
        holder.status.setText(this.wifiscanresult.get(position).getstatus() ? "Connected" : "");

    }

    @Override
    public int getItemCount() {
        return wifiscanresult.size();
    }


    class Viewhloder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ssid;
        TextView status;

        public Viewhloder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ssid = (TextView) itemView.findViewById(R.id.wifi_ssid);
            status = (TextView) itemView.findViewById(R.id.wifi_status);
        }

        @Override
        public void onClick(View v) {

            if (!wifiscanresult.get(getAdapterPosition()).getstatus()){
                desiredMacAddress = wifiscanresult.get(getAdapterPosition()).getBssid();
                ShowInputDialog(wifiscanresult.get(getAdapterPosition()).getSsid());
            }

        }


    }


    private void ShowInputDialog(final String ssid) {

        View view = LayoutInflater.from(context).inflate(R.layout.enterwifipassworddialog, null, false);
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


        alertDialog = new AlertDialog.Builder(context)
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

                        dialog.dismiss();
                        ConnectWifi(ssid,passwordwifi.getText().toString().trim());
                        showprogrssConnecting(ssid);

                    }

                }).show();

    } // show dialog input password


    private void ConnectWifi(String ssid, String password) {

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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


    class WifiBroadcastReceiver extends BroadcastReceiver {

        private  WifiManager wifiManager;

        public WifiBroadcastReceiver(){
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {

                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

                if (SupplicantState.isValidState(state)) {

                    if (state == SupplicantState.COMPLETED){
                        boolean connected = checkConnectedToDesiredWifi();
                        if (connected){
                            wificonnecting.dismiss();
                        }
                    }

                }

           int stateerror = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,0);

                if (stateerror == WifiManager.ERROR_AUTHENTICATING){
                    wifiManager.disableNetwork(netId);
                    wifiManager.disconnect();
                    wificonnecting.dismiss();
                    showWifiAuthErrorDialog();
                }

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


    private void showWifiAuthErrorDialog(){

        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(context)
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

        wificonnecting = new ProgressDialog(context);
        wificonnecting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wificonnecting.setMessage("Connecting to " + ssid);
        wificonnecting.setCancelable(false);
        wificonnecting.show();

    } // show progress connecting to specific wifi




}
