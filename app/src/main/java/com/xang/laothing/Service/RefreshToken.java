package com.xang.laothing.Service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.xang.laothing.Activity.BannerActivity;
import com.xang.laothing.Api.ApiService;
import com.xang.laothing.Api.reponse.SignUpAndLoginResponse;
import com.xang.laothing.Api.request.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xang on 15/06/2017.
 */

public class RefreshToken extends IntentService {


    private FirebaseAuth auth;
    private String TAG = "refresh token";

    public RefreshToken() {
        super("Refresh API Token");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            refreshApiToken();
        }catch (Exception e){
            Log.d(TAG,e.getLocalizedMessage());
        }

    }


    private void refreshApiToken(){

        if (auth.getCurrentUser() !=null)
        {
            //have login
            String uid = auth.getCurrentUser().getUid();
            ApiService.getRouterServiceApi().Login(new LoginRequest(uid))
                    .enqueue(new Callback<SignUpAndLoginResponse>() {
                        @Override
                        public void onResponse(Call<SignUpAndLoginResponse> call, Response<SignUpAndLoginResponse> response) {

                            if (response!=null && response.isSuccessful()){

                                SignUpAndLoginResponse signIn = response.body();
                                if (signIn.err != 1){
                                    SharePreferentService.SaveToken(RefreshToken.this,signIn.token);
                                    Log.d(TAG,signIn.token);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<SignUpAndLoginResponse> call, Throwable t) {
                            Log.d(TAG,t.getLocalizedMessage());
                        }
                    });

        } //if login

    } // refresh api toekn


}
