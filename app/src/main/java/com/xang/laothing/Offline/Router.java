package com.xang.laothing.Offline;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import com.xang.laothing.Offline.response.SmatrtSwitchOfflineResponse;

/**
 * Created by xang on 04/05/2017.
 */

public interface Router {

    @FormUrlEncoded
    @POST("controller")
    Call<SmatrtSwitchOfflineResponse> SendCommand(@Field("swn") String swn , @Field("cmd") String cmd);

}
