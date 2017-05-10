package com.xang.laothing.Service;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by xang on 10/05/2017.
 */

public class IdentifierService {


    public static String getDeivceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


}
