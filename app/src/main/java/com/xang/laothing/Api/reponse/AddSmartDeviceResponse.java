package com.xang.laothing.Api.reponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 04/05/2017.
 */

public class AddSmartDeviceResponse {


    @SerializedName("err")
    public int err;
    @SerializedName("device")
    public Device device;


    public class Device {

        @SerializedName("_id")
        public String _id;
        @SerializedName("sdid")
        public String sdid;
        @SerializedName("type")
        public int type;
        @SerializedName("regis")
        public boolean regis;

    }


}
