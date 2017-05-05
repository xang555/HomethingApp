package com.xang.laothing.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarContext;

/**
 * Created by xang on 05/05/2017.
 */

public class HomethingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }


}
