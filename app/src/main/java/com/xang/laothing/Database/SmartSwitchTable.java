package com.xang.laothing.Database;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xang on 03/05/2017.
 */


public class SmartSwitchTable extends SugarRecord {

    @Unique
    String ssid;
    String btnOneName;
    String btnTwoName;
    String btnThreeName;
    String btnFourName;

    SmartDeviceTable smartDevice;

    public SmartSwitchTable(){}

    public SmartSwitchTable(String ssid, String btnOneName, String btnTwoNamem, String btnThreeName, String btnFourName){
        this.ssid = ssid;
        this.btnOneName = btnOneName;
        this.btnTwoName = btnTwoNamem;
        this.btnThreeName = btnThreeName;
        this.btnFourName = btnFourName;

    }


}
