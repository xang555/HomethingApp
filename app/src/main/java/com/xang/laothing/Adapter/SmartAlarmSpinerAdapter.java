package com.xang.laothing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xang.laothing.Model.SmartAlarmModel;
import com.xang.laothing.R;

import java.util.List;

/**
 * Created by xang on 10/05/2017.
 */

public class SmartAlarmSpinerAdapter extends BaseAdapter {

    private Context context;
    private List<SmartAlarmModel> alarms;

    public SmartAlarmSpinerAdapter(Context context, List<SmartAlarmModel> alarmModels){
        this.alarms = alarmModels;
        this.context = context;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewhloder vh = null;
        if (convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.smartalarm_dropdown_item,parent,false);
            vh = new viewhloder(convertView);
            convertView.setTag(vh);
        }else {
            vh = (viewhloder)convertView.getTag();
        }
        vh.label.setText(alarms.get(position).getName());
        return convertView;
    }

    class viewhloder {
        TextView label;
        public viewhloder(View view){
            label = (TextView)view.findViewById(R.id.gas_item_name);
        }
    }


}
