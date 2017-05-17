package com.xang.laothing.Database;

import com.orm.SugarRecord;

/**
 * Created by xang on 17/05/2017.
 */

public class userTabel extends SugarRecord {

    public String uname;
    public String fname;
    public String lname;
    public String email;

    public userTabel(){}

    public userTabel(String uname, String fname, String lname, String email){
        this.uname = uname;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
    }

}
