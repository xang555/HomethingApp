package com.xang.laothing.AppState;

import android.os.Bundle;
import android.os.Parcelable;

import com.xang.laothing.Model.SmartDeviceModel;

import org.parceler.Parcels;

import java.util.List;

import icepick.Bundler;

/**
 * Created by xang on 6/9/2017.
 */

public class SmartDeviceDataLists implements Bundler<List<SmartDeviceModel>> {
    @Override
    public void put(String s, List<SmartDeviceModel> smartDeviceModels, Bundle bundle) {
        Parcelable data = Parcels.wrap(smartDeviceModels);
        bundle.putParcelable(s,data);
    }

    @Override
    public List<SmartDeviceModel> get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
