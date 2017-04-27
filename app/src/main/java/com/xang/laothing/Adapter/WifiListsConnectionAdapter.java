package com.xang.laothing.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xang.laothing.R;

import java.util.List;

/**
 * Created by xang on 27/04/2017.
 */

public class WifiListsConnectionAdapter extends RecyclerView.Adapter<WifiListsConnectionAdapter.Viewhloder>{

    private List<String> wifiscanresult ;
    private Context context;

    public WifiListsConnectionAdapter(Context context, List<String> wifiscanresult){
        this.wifiscanresult = wifiscanresult;
        this.context = context;
    }

    @Override
    public Viewhloder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.wifi_connection_items,parent,false);
        Viewhloder vh = new Viewhloder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(Viewhloder holder, int position) {
        holder.ssid.setText(this.wifiscanresult.get(position));
    }

    @Override
    public int getItemCount() {
        return wifiscanresult.size();
    }



    static class Viewhloder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView ssid ;

        public Viewhloder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ssid = (TextView)itemView.findViewById(R.id.wifi_ssid);
        }

        @Override
        public void onClick(View v) {


        }


    }

}
