package com.xang.laothing.Service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by xang on 09/05/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {


    private static final String PUSH_NOTIFICATION_SETTING = "push_noti";

    @Override
    public void onTokenRefresh() {

       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FirebaseInstanceIDService.this);
        if (preferences.getBoolean(PUSH_NOTIFICATION_SETTING,false)){

            String token = FirebaseInstanceId.getInstance().getToken();
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();



        }


    }

}
