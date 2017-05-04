package com.xang.laothing.Offline;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.xang.laothing.Offline.request.SmartSwitchOfflineRequest;
import com.xang.laothing.Offline.response.SmatrtSwitchOfflineResponse;

/**
 * Created by xang on 04/05/2017.
 */

public interface Router {

    @POST("controller")
    Call<SmatrtSwitchOfflineResponse> SendCommand(@Body SmartSwitchOfflineRequest request);

}
