package com.xang.laothing.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xang on 02/05/2017.
 */

public class ApiService {


    public static Router getRouterServiceApi(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.getbaseUri())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Router router = retrofit.create(Router.class);

        return router;
    }


}
