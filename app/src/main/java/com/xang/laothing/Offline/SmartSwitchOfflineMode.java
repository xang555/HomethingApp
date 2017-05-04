package com.xang.laothing.Offline;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xang on 04/05/2017.
 */

public class SmartSwitchOfflineMode {

public static Router offlineService(){

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(config.getofflineUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    Router router = retrofit.create(Router.class);

    return router;
}


}
