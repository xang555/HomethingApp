package com.xang.laothing.Database;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xang on 03/05/2017.
 */


public class SmartSwitchTable extends SugarRecord {

    @Unique
    public String sdid;
    public String btnOneName;
    public String btnTwoName;
    public String btnThreeName;
    public String btnFourName;

    SmartDeviceTable smartDevice;

    public SmartSwitchTable(){}

    public SmartSwitchTable(String sdid, String btnOneName, String btnTwoNamem, String btnThreeName, String btnFourName){
        this.sdid = sdid;
        this.btnOneName = btnOneName;
        this.btnTwoName = btnTwoNamem;
        this.btnThreeName = btnThreeName;
        this.btnFourName = btnFourName;

    }


}
