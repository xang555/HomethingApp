package com.xang.laothing.Api.reponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 17/05/2017.
 */

public class ProfileResponse {

    @SerializedName("err")
    public int err;
    @SerializedName("user")
    public user user;


    public class user {

        @SerializedName("uname")
        public String uname;
        @SerializedName("fname")
        public String fname;
        @SerializedName("lname")
        public String lname;
        @SerializedName("email")
        public String email;
        @SerializedName("uid")
        public String uid;

    }


}
