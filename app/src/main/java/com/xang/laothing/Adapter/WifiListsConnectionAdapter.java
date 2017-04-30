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

    public interface WifiAdapterListentner {
        public void onItemClick(WifiScanModel wifiScanModel, int position);
    }

    private WifiAdapterListentner wifiListentner;


    public WifiListsConnectionAdapter(Context context, List<WifiScanModel> wifiscanresult) {
        this.wifiscanresult = wifiscanresult;
        this.context = context;
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

            if (wifiListentner !=null){
                wifiListentner.onItemClick(wifiscanresult.get(getAdapterPosition()), getAdapterPosition());
            }

        }


    }


    public void setOnItemClickListentner(WifiAdapterListentner listentner){
        this.wifiListentner = listentner;
    }


}
