package com.xang.laothing.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.xang.laothing.R;

/**
 * Created by xang on 07/05/2017.
 */

public class SettingTimeFragmentDialog extends DialogFragment {


    private static final String POSITION_REF = "position_ref";
    private RadioButton radio_on , radio_off;
    private RadioButton[] radio = {radio_on,radio_off};
    private TimePicker timePicker;
    private Button cancle,ok;
    private int hour;
    private int mn;
    private int position;
    private boolean datetimepickerischange = false;


    public interface settingTimeListener {
        public void onUserSettingTimeCompletted(int hour , int minute,boolean status,int position);
    }

    private settingTimeListener timeListener;


    public static SettingTimeFragmentDialog newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(POSITION_REF,position);
        SettingTimeFragmentDialog fragment = new SettingTimeFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(POSITION_REF);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting_time_dialog,container,false);
        radio[0] = (RadioButton)view.findViewById(R.id.radio_status_on);
        radio[1] = (RadioButton)view.findViewById(R.id.radio_status_off);
        timePicker = (TimePicker)view.findViewById(R.id.timePicker);
        cancle = (Button)view.findViewById(R.id.cancle_button);
        ok = (Button)view.findViewById(R.id.ok_button);
        timePicker.setIs24HourView(true);
        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    hour = hourOfDay;
                    mn = minute;
                datetimepickerischange = true;
            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean status;

                if (radio[0].isChecked()) {
                    status = true;
                }else {
                    status = false;
                }

                if (!datetimepickerischange){
                    hour = timePicker.getHour();
                    mn = timePicker.getMinute();
                }
                timeListener.onUserSettingTimeCompletted(hour,mn,status,position);
                dismiss();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            timeListener = (settingTimeListener)getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException("Must implement settingTimeListener");
        }

    }


}
