package com.xang.laothing.FactorySnipView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by xang on 02/05/2017.
 */

public class AlertDialogService {


    public static void ShowAlertDialog(Context context, String title,String messg){

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(messg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }


}
