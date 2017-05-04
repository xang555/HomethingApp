package com.xang.laothing.Offline.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 04/05/2017.
 */

public class SmartSwitchOfflineRequest {

    @SerializedName("swn")
    public String swn;
    @SerializedName("cmd")
    public String cmd;

    public SmartSwitchOfflineRequest(String swn, String cmd){
        this.swn = swn;
        this.cmd = cmd;
    }


}
