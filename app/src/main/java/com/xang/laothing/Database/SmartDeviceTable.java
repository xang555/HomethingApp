package com.xang.laothing.Database;


import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xang on 03/05/2017.
 */

public class SmartDeviceTable extends SugarRecord{

    @Unique
    String ssid;
    String regis;
    String type;
    String name;

    public SmartDeviceTable(){}

    public SmartDeviceTable(String ssid, String regis , String type, String name){
        this.ssid = ssid;
        this.regis = regis;
        this.type = type;
        this.name = name;
    }


}
