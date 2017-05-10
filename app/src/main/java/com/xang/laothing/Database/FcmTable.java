package com.xang.laothing.Database;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by xang on 10/05/2017.
 */

public class FcmTable extends SugarRecord{

    @Unique
    public String sdid;
    public boolean regis;

    public FcmTable(){}

    public FcmTable(String sdid, boolean regis){
        this.sdid = sdid;
        this.regis = regis;
    }


}
