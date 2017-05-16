package com.xang.laothing.Model;

/**
 * Created by xang on 03/05/2017.
 */

public class SmartDeviceModel {

    private String sdid;
    private String name;
    private int type;
    private String deviceCode;

    public SmartDeviceModel(){}

    public void setSdid(String sdid){
        this.sdid = sdid;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setType(int type){
        this.type = type;
    }



    public void setDeviceCode(String deviceCode){
        this.deviceCode = deviceCode;
    }

    public String getSdid(){
        return this.sdid;
    }

    public String getName(){
        return this.name;
    }

    public int getType(){
        return this.type;
    }

    public String getDeviceCode(){
        return this.deviceCode;
    }


}
