package com.xang.laothing.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by xang on 07/05/2017.
 */

@IgnoreExtraProperties
public class SchedulerTimeFirebaseModel {

    public int hour;
    public int minute;

    public SchedulerTimeFirebaseModel(){}

    public SchedulerTimeFirebaseModel(int hour , int minute){
        this.hour = hour;
        this.minute = minute;
    }

}
