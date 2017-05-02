package com.xang.laothing.Api.reponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 02/05/2017.
 */

public class SignUpAndLoginResponse {

    @SerializedName("err")
    public int err;
    @SerializedName("token")
    public String token;


}
