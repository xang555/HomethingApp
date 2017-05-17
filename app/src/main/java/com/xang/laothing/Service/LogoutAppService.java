package com.xang.laothing.Service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.xang.laothing.Activity.LoginActivity;
import com.xang.laothing.Activity.MainActivity;
import com.xang.laothing.Database.FcmTable;
import com.xang.laothing.Database.SmartDeviceTable;
import com.xang.laothing.Database.SmartSwitchTable;
import com.xang.laothing.Database.userTabel;

import java.util.Iterator;

/**
 * Created by xang on 17/05/2017.
 */

public class LogoutAppService {


    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Context context;

    public interface onLogoutListener{
        public void Logouted();
    }


    public LogoutAppService(FirebaseDatabase database, FirebaseAuth auth, Context context){
        this.database = database;
        this.auth =auth;
        this.context =context;
    }

    public void Logout(final onLogoutListener listener){

        new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Logout from homething ?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                SmartDeviceTable.deleteAll(SmartDeviceTable.class); //delete data smart device
                SmartSwitchTable.deleteAll(SmartSwitchTable.class); // delete data smart switch
                userTabel.deleteAll(userTabel.class); //delete user table
                DeleteNotificationToken(); // delete fcm token
                SharePreferentService.setFirstLoad(context,true); // set first load
                SharePreferentService.SaveToken(context,""); // delete token
                SharePreferentService.setIsFirstLoadSmartDevice(context,true);
                auth.signOut();

                listener.Logouted();


            }
        }).show();


    }


    private void DeleteNotificationToken(){

        Iterator<FcmTable> fcms = FcmTable.findAll(FcmTable.class);
        while (fcms.hasNext()){
            FcmTable fcm = fcms.next();
            deletNotiTokenFromFirebase(fcm.sdid);
        }
        FcmTable.deleteAll(FcmTable.class); // delete token
    } // delete noti token when logout


    private void deletNotiTokenFromFirebase(String sdid){

        database.getReference(sdid).child("sensor").child("alert").child(IdentifierService.getDeivceId(context)).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DLEET TOKEN","delete token suucessfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DLEET TOKEN","delete token failure");
                    }
                });

    } //delete notify token from firebase


}
