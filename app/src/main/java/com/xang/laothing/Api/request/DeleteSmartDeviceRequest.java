package com.xang.laothing.Api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 04/05/2017.
 */

public class DeleteSmartDeviceRequest {


    @SerializedName("sdid")
    public String sdid;

    public DeleteSmartDeviceRequest(String sdid){
        this.sdid = sdid;
    }

}
