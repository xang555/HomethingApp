package com.xang.laothing.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xang.laothing.R;


public class SmartDeviceListsFragment extends Fragment {


    public SmartDeviceListsFragment() {
        // Required empty public constructor
    }

     public static SmartDeviceListsFragment newInstance() {
        SmartDeviceListsFragment fragment = new SmartDeviceListsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_smart_device_lists, container, false);
    }

}
