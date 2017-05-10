package com.xang.laothing.Model;

/**
 * Created by xang on 10/05/2017.
 */

public class SmartAlarmModel {


    private String sdid;
    private String name;

    public SmartAlarmModel(){};

    public void setSdid(String sdid){
        this.sdid = sdid;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSdid(){
        return this.sdid;
    }

    public String getName(){
        return this.name;
    }


}
