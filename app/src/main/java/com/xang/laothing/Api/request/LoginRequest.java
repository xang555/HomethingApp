package com.xang.laothing.Api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 02/05/2017.
 */

public class LoginRequest {

    @SerializedName("uid")
    String uid;

    public LoginRequest(String uid){
        this.uid = uid;
    }

}
