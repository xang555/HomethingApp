package com.xang.laothing.Api;

import com.xang.laothing.Api.reponse.SignUpResponse;
import com.xang.laothing.Api.request.RequestSignup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by xang on 02/05/2017.
 */

public interface Router {

    @POST("user/singup")
    Call<SignUpResponse> Sigup(@Body RequestSignup requestSignup);

}
