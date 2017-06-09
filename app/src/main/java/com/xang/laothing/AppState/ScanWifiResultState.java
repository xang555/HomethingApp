package com.xang.laothing.AppState;

import android.os.Bundle;
import android.os.Parcelable;

import com.xang.laothing.Model.WifiScanModel;

import org.parceler.Parcels;

import java.util.List;

import icepick.Bundler;

/**
 * Created by xang on 09/06/2017.
 */

public class ScanWifiResultState implements Bundler<List<WifiScanModel>> {

    @Override
    public void put(String s, List<WifiScanModel> wifiScanModel, Bundle bundle) {
        Parcelable wifi = Parcels.wrap(wifiScanModel);
        bundle.putParcelable(s,wifi);
    }

    @Override
    public List<WifiScanModel> get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }

}
