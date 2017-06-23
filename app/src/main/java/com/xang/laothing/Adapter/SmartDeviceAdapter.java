package com.xang.laothing.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xang.laothing.Model.SmartDeviceModel;
import com.xang.laothing.R;

import java.util.List;

/**
 * Created by xang on 03/05/2017.
 */

public class SmartDeviceAdapter extends RecyclerView.Adapter<SmartDeviceAdapter.viewholder> {

    private List<SmartDeviceModel> smartDevicedatalists;
    private Context context;


    public interface onItemClickListener{
        public void itemClick(int position);
    }

    public interface onSettingClickListener{
        public void onClick(int position);
    }

    public interface onUpdatestatusListener{
        public void onUpdate(viewholder holder, int position);
    }

    private onItemClickListener itemClickListener;
    private onSettingClickListener onSettingClickListener;
    private onUpdatestatusListener onUpdatestatusListener;

    public SmartDeviceAdapter(Context context, List<SmartDeviceModel> smartDevicedatalists){
        this.smartDevicedatalists = smartDevicedatalists;
        this.context =context;
    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.smartdevice_list_item,parent,false);
         viewholder vh = new viewholder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final viewholder holder, int position) {

        int image = 0;

        switch (smartDevicedatalists.get(position).getType()){

            case 0:
                image = R.drawable.ic_switch;
                break;
            case 1 :
                image = R.drawable.ic_temp_and_humi;
                break;
            case 2 :
                image = R.drawable.ic_gass_sensor;
                break;
            case 3 :
                image = R.drawable.ic_alarm;
                break;

        }

        holder.cover.setImageResource(image);

        holder.label.setText(smartDevicedatalists.get(position).getName());

        holder.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingClickListener.onClick(holder.getAdapterPosition());
            }
        });

        onUpdatestatusListener.onUpdate(holder,position);

    }

    @Override
    public int getItemCount() {
        return smartDevicedatalists.size();
    }



    public  class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

         ImageView setting;
         public ImageView status;
         ImageView cover;
         TextView label;

         viewholder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            setting = (ImageView)itemView.findViewById(R.id.item_setting);
            status = (ImageView)itemView.findViewById(R.id.state_device_active);
            cover = (ImageView)itemView.findViewById(R.id.img_device_cover);
            label  = (TextView) itemView.findViewById(R.id.item_label);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.itemClick(getAdapterPosition());
        }

    }


    public void setItemClickListener(onItemClickListener listener){
        this.itemClickListener= listener;
    }

    public void setOnSettingClickListener(onSettingClickListener listener){
        this.onSettingClickListener = listener;
    }

    public void setOnUpdatestatusListener(onUpdatestatusListener listener){
        this.onUpdatestatusListener = listener;
    }


}
