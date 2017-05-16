package com.xang.laothing.Api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 04/05/2017.
 */

public class AddSmartDeviceRequest {

    @SerializedName("sdid")
    public String sdid;
    @SerializedName("sharecode")
    public String sharecode;

    public AddSmartDeviceRequest(String sdid, String sharecode){
        this.sdid = sdid;
        this.sharecode = sharecode;
    }

}
