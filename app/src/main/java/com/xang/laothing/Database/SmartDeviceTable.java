package com.xang.laothing.Database;


import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xang on 03/05/2017.
 */

public class SmartDeviceTable extends SugarRecord{

    @Unique
    public String sdid;
    public String sharecode;
    public int type;
    public String name;

    public SmartDeviceTable(){}

    public SmartDeviceTable(String sdid, String sharecode , int type, String name){
        this.sdid = sdid;
        this.sharecode = sharecode;
        this.type = type;
        this.name = name;
    }


}
