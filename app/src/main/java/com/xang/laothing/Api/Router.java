package com.xang.laothing.Api;

import com.xang.laothing.Api.reponse.DevicesResponse;
import com.xang.laothing.Api.reponse.SignUpAndLoginResponse;
import com.xang.laothing.Api.request.LoginRequest;
import com.xang.laothing.Api.request.RequestSignup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xang on 02/05/2017.
 */

public interface Router {

    @POST("user/singup")
    Call<SignUpAndLoginResponse> Sigup(@Body RequestSignup requestSignup);

    @POST("user/login")
    Call<SignUpAndLoginResponse> Login(@Body LoginRequest loginRequest);

    @GET("devices")
    Call<DevicesResponse> getDeviceByUser(@Header("Authorization") String token);


}
