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
    private static final String IS_USE_SMART_SWITCH = "use_switch";
    private static final String IS_USE = "f_use";
    private static final String FINGER_PRINT_CODE = "finger_code";
    private static final String FINGER_PRINT = "finger_key";
    private static final String USER_EMAIL = "user_mail";
    private static final String EMAIL_KEY = "mail_key";

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

    public static void setIsFirstLoadSmartDevice(Context context, boolean isuse){
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_USE_SMART_SWITCH,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_USE,isuse);
        editor.apply();
    }

    public static boolean isFirstUseSmartSwitch(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_USE_SMART_SWITCH,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_USE,true);
    }

    public static void setSaveFingerPrint(Context context,String finger){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FINGER_PRINT_CODE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String fingercode = "";
        if (finger.trim().length() > 0){
            fingercode = secureService.encrypt(finger,secureService.getcode());
        }
        editor.putString(FINGER_PRINT,fingercode);
        editor.apply();
    }


    public static String getFingerPrint(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FINGER_PRINT_CODE,Context.MODE_PRIVATE);
        String fingerprintcode = sharedPreferences.getString(FINGER_PRINT,"");
        if (fingerprintcode.trim().length() <=0){
            return "";
        }
        return secureService.decrypt(fingerprintcode,secureService.getcode());
    }

    public static void saveEmail(Context context,String email){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_EMAIL,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL_KEY,email);
        editor.apply();
    }


    public static String getUserEmail(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_EMAIL,Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL_KEY,"");
    }


}
