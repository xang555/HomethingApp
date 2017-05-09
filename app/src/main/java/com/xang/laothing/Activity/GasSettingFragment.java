package com.xang.laothing.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.xang.laothing.R;

import java.util.HashMap;

/**
 * Created by xang on 02/05/2017.
 */

public class GasSettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String SDID_KEY_DEVICE = "sdid_key_setting";
    private static final CharSequence PERSIS_SMART_ALARM_LINK = "pref_key_persis_smart_alarm";
    private FirebaseDatabase database;
    private String sdid="";

    public static GasSettingFragment newInstance(String sdid) {

        Bundle args = new Bundle();
        args.putString(SDID_KEY_DEVICE,sdid);
        GasSettingFragment fragment = new GasSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        sdid = getArguments().getString(SDID_KEY_DEVICE);
        setSmartAlarmInfo(sdid);

        addPreferencesFromResource(R.xml.preference);

    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {




    }


    private void setSmartAlarmInfo(String sdid){

        database.getReference(sdid).child("sensor").child("SmartAlarmlinks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        HashMap<String,String> links = (HashMap<String, String>) dataSnapshot.getValue();
                        Preference connectionPref = findPreference(PERSIS_SMART_ALARM_LINK);
                        connectionPref.setSummary(links.size()+" device");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    } //sett sumary smart slarm count



}
