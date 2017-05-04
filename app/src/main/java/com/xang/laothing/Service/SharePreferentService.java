package com.xang.laothing.Service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xang on 02/05/2017.
 */

public class SharePreferentService {


    private static final String TOKEN_PREF = "token_pref";
    private static final String TOKEN = "token";
    private static final String IS_FIRST_LOAD_SMART_DEVICE = "is_first_load";
    private static final String IS_FIRST = "is_first";

    public static void SaveToken(Context context , String token){

        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN,token);
        editor.apply();

    }


    public static String getToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREF,Context.MODE_PRIVATE);
        String token = "Bearer "+ sharedPreferences.getString(TOKEN,"");
        return token;
    }


    public static void setFirstLoad(Context context, boolean isfirst){
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_FIRST_LOAD_SMART_DEVICE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST,isfirst);
        editor.apply();
    }

    public static boolean isFirstLoad(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_FIRST_LOAD_SMART_DEVICE,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_FIRST,true);
    }

}