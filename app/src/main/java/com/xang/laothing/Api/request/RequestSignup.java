package com.xang.laothing.Api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xang on 02/05/2017.
 */

public class RequestSignup {

    @SerializedName("uname")
    public String uname;
    @SerializedName("lname")
    public String lname;
    @SerializedName("fname")
    public String fname;
    @SerializedName("email")
    public String email;
    @SerializedName("uid")
    public String uid;


    public RequestSignup(String uname, String lname, String fname,String email,String uid){
        this.uname = uname;
        this.lname = lname;
        this.fname = fname;
        this.email = email;
        this.uid = uid;
    }


}
