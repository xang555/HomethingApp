package com.xang.laothing.FactorySnipView;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by xang on 02/05/2017.
 */

public class Depending {


    public static ProgressDialog showDependingProgressDialog(Context context, String msg){

        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();

        return progressDialog;

    }


}
