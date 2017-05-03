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
    public void onBindViewHolder(viewholder holder, final int position) {

        holder.label.setText(smartDevicedatalists.get(position).getName());

        holder.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingClickListener.onClick(position);
            }
        });

        if (smartDevicedatalists.get(position).isActive()){
            holder.status.setColorFilter(Color.BLUE);
        }else {
            holder.status.setColorFilter(Color.RED);
        }

        onUpdatestatusListener.onUpdate(holder,position);

    }

    @Override
    public int getItemCount() {
        return smartDevicedatalists.size();
    }



    public  class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

       public ImageView setting;
        public ImageView status;
        public ImageView cover;
        public TextView label;

        public viewholder(View itemView) {
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
