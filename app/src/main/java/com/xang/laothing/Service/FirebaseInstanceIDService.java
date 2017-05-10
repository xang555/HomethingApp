package com.xang.laothing.Service;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.xang.laothing.Database.FcmTable;

import java.util.List;

/**
 * Created by xang on 09/05/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

    String token = FirebaseInstanceId.getInstance().getToken();
        if (token!=null){
            List<FcmTable> fcms = FcmTable.find(FcmTable.class,"regis = ?","1");
            for (int i =0 ;i < fcms.size();i++){
                handleSendTokenToFirebaes(fcms.get(i).sdid,token);
            }

        }

    }

    private void handleSendTokenToFirebaes(String sdid,String token) {

        FirebaseDatabase database =FirebaseDatabase.getInstance();
        database.getReference(sdid).child("sensor").child("alert").child(IdentifierService.getDeivceId(FirebaseInstanceIDService.this))
                .setValue(token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NOTIFICATION ID","set notification successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("NOTIFICATION ID","set notification Failure "+e.getMessage());
            }
        });


    } //handle send token

}
