package com.xang.laothing.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xang.laothing.R;

/**
 * Created by xang on 02/05/2017.
 */

public class GasSettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{


    public static GasSettingFragment newInstance() {
        GasSettingFragment fragment = new GasSettingFragment();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        Toast.makeText(getActivity(),key,Toast.LENGTH_SHORT).show();

    }


}
