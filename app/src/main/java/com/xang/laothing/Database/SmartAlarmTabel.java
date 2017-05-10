package com.xang.laothing.Database;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xang on 10/05/2017.
 */

public class SmartAlarmTabel extends SugarRecord {

    @Unique
    public String smalId;
    public String gasId;

    public SmartAlarmTabel(){};

    public SmartAlarmTabel(String smalId, String gasId){
        this.smalId = smalId;
        this.gasId = gasId;
    }


}
