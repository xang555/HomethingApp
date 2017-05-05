package com.xang.laothing.Api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 05/05/2017.
 */

public class EditSmartDeviceRequest {

    @SerializedName("sdid")
    public String sdid;
    @SerializedName("dname")
    public String dname;

    public EditSmartDeviceRequest(String sdid , String dname){
        this.sdid = sdid;
        this.dname = dname;
    }


}
