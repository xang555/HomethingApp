package com.xang.laothing.FunctionService;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xang on 02/05/2017.
 */

public class SharePreferentService {


    private static final String TOKEN_PREF = "token_pref";
    private static final String TOKEN = "token";

    public static void SaveToken(Context context , String token){

        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN,token);
        editor.apply();

    }


    public static String getToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN,"");
    }


}
