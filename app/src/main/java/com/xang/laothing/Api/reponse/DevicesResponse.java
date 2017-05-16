package com.xang.laothing.Api.reponse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xang on 03/05/2017.
 */

public class DevicesResponse {

    @SerializedName("err")
   public int err;
    @SerializedName("devices")
    public List<deviceLists> devices;

    public class deviceLists {

        @SerializedName("_id")
        public String _id;
        @SerializedName("sdid")
        public String sdid;
        @SerializedName("type")
        public int type;
        @SerializedName("nicname")
        public String nicname;
        @SerializedName("sharecode")
        public String sharecode;

    }


}
