package com.xang.laothing.Api;

import com.xang.laothing.Api.reponse.AddSmartDeviceResponse;
import com.xang.laothing.Api.reponse.DeleteSmartDeviceResponse;
import com.xang.laothing.Api.reponse.DevicesResponse;
import com.xang.laothing.Api.reponse.EditSmartDeviceResponse;
import com.xang.laothing.Api.reponse.SignUpAndLoginResponse;
import com.xang.laothing.Api.request.AddSmartDeviceRequest;
import com.xang.laothing.Api.request.DeleteSmartDeviceRequest;
import com.xang.laothing.Api.request.EditSmartDeviceRequest;
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

    @POST("app/device/add")
    Call<AddSmartDeviceResponse> AddSmartDevice(@Header("Authorization") String token, @Body AddSmartDeviceRequest request);

    @POST("user/device/delete")
    Call<DeleteSmartDeviceResponse> DeleteSmartDevice(@Header("Authorization") String token, @Body DeleteSmartDeviceRequest request);

    @POST("user/device/update")
    Call<EditSmartDeviceResponse> EditSmartDevice(@Header("Authorization") String token, @Body EditSmartDeviceRequest request);

}
